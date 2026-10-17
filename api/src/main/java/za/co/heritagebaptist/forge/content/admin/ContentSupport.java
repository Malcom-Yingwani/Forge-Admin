package za.co.heritagebaptist.forge.content.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import za.co.heritagebaptist.forge.auth.*;
import za.co.heritagebaptist.forge.common.*;
import za.co.heritagebaptist.forge.sync.SyncHook;
import za.co.heritagebaptist.forge.content.blog.BlogPost;
import za.co.heritagebaptist.forge.content.document.Document;
import za.co.heritagebaptist.forge.content.event.Event;
import za.co.heritagebaptist.forge.content.person.Person;
import za.co.heritagebaptist.forge.content.sermon.SermonSeries;
@Component
public class ContentSupport {
    public final EntityManager em;
    private final SlugService slugs;
    private final AuditService audit;
    private final AppUserRepository users;
    private final ObjectMapper json;
    private final ObjectProvider<SyncHook> hook;
    public ContentSupport(EntityManager em,SlugService slugs,AuditService audit,AppUserRepository users,ObjectMapper json,ObjectProvider<SyncHook> hook) {
        this.em=em; this.slugs=slugs; this.audit=audit; this.users=users; this.json=json; this.hook=hook;
    }
    public <T> T reference(Class<T> type,UUID id) { if(id==null) return null; T result=em.find(type,id); if(result==null) throw new NotFoundException("Referenced record not found"); return result; }
    public AppUser actor() {
        var authentication=SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null&&authentication.getPrincipal() instanceof AuthPrincipal principal) return users.findById(principal.id()).orElseThrow(AuthFailure::new);
        return null;
    }
    public String slug(Class<?> type,String value,UUID id) {
        return slugs.uniqueSlug(value,candidate -> em.createQuery("select count(e) from "+type.getSimpleName()+" e where e.slug=:slug and e.id<>:id",Long.class).setParameter("slug",candidate).setParameter("id",id).getSingleResult()>0);
    }
    public Map<String,Object> snapshot(Object dto) { return dto==null?Map.of():json.convertValue(dto,new TypeReference<Map<String,Object>>() {}); }
    public void changed(String type,BaseEntity entity,String action,Map<String,Object> before,Object after) {
        Map<String,Object> current=snapshot(after),diff=new LinkedHashMap<>();
        Set<String> keys=new LinkedHashSet<>(before.keySet()); keys.addAll(current.keySet());
        for(String key:keys) if(!Objects.equals(before.get(key),current.get(key))) diff.put(key,Arrays.asList(before.get(key),current.get(key)));
        audit.record(actor(),action,type,entity.getId(),diff);
        if("DELETE".equals(action)) hook.getObject().onDeleted(type,entity.getId()); else hook.getObject().onChanged(entity);
    }
    public void validate(BaseEntity entity) {
        if(entity instanceof Document e && (e.getBody()==null||e.getBody().isBlank())&&e.getPdf()==null) throw new ContentExceptionHandler.Invalid("body","Add text or a PDF");
        if(entity instanceof Person e && e.isPublished()&&e.getConsentAt()==null) throw new ContentExceptionHandler.Invalid("consentAt","Consent is required before publishing a person");
        if(entity instanceof Person e && e.getConsentAt()!=null&&e.getConsentAt().isAfter(Instant.now())) throw new ContentExceptionHandler.Invalid("consentAt","Consent cannot be in the future");
        if(entity instanceof Event e && e.getEndsAt()!=null&&e.getEndsAt().isBefore(e.getStartsAt())) throw new ContentExceptionHandler.Invalid("endsAt","The end must follow the start");
        if(entity instanceof SermonSeries e && e.getStartDate()!=null&&e.getEndDate()!=null&&e.getEndDate().isBefore(e.getStartDate())) throw new ContentExceptionHandler.Invalid("endDate","The end must follow the start");
        if(entity instanceof BlogPost e && e.isPublished()&&e.getPublishedAt()==null) e.setPublishedAt(Instant.now());
    }
    public void schedule(BaseEntity entity) {
        if(!(entity instanceof BlogPost e)) return;
        em.createNativeQuery("delete from scheduled_publication where blog_id=:id").setParameter("id",e.getId()).executeUpdate();
        if(e.isPublished()&&e.getPublishedAt()!=null&&e.getPublishedAt().isAfter(Instant.now())) em.createNativeQuery("insert into scheduled_publication(blog_id,scheduled_at) values (:id,:at)").setParameter("id",e.getId()).setParameter("at",e.getPublishedAt()).executeUpdate();
    }
}
