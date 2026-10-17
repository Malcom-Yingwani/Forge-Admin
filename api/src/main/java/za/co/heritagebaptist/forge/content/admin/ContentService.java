package za.co.heritagebaptist.forge.content.admin;
import jakarta.persistence.criteria.Predicate;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import za.co.heritagebaptist.forge.common.*;

@Transactional(readOnly=true)
public abstract class ContentService<E extends BaseEntity,R,D,P> {
    protected final ContentSupport support;
    protected final JpaRepository<E,UUID> repository;
    protected final ContentMapper<E,R,D,P> mapper;
    private final Class<E> entityType;
    private final String type;
    private final Set<String> sortable;
    private final List<String> searchable;
    protected ContentService(ContentSupport support,JpaRepository<E,UUID> repository,ContentMapper<E,R,D,P> mapper,Class<E> entityType,String type,Set<String> sortable,List<String> searchable) {
        this.support=support; this.repository=repository; this.mapper=mapper; this.entityType=entityType; this.type=type; this.sortable=sortable; this.searchable=searchable;
    }
    protected E entity(UUID id) { return repository.findById(id).orElseThrow(() -> new NotFoundException("Content not found")); }
    protected abstract E fresh();
    protected abstract Long version(R request);
    protected void setPublished(E entity,boolean published) { throw new ContentExceptionHandler.Invalid("published","This type does not support publishing",400); }
    protected void setOrder(E entity,int order) { throw new ContentExceptionHandler.Invalid("order","This type does not support ordering",400); }
    public String type() { return type; }
    public Class<E> entityType() { return entityType; }
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public D get(UUID id) { return mapper.admin(entity(id)); }
    public P publicDto(UUID id) { return mapper.publicDto(entity(id)); }
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public PageResponse<D> list(String q,String sort,int page,int size) {
        if(page<0||page>100000||size<1||size>100) throw new ContentExceptionHandler.Invalid("page","Invalid page or size",400);
        String[] order=sort.split(",",-1);
        if(order.length>2||!sortable.contains(order[0])||(order.length==2&&!Set.of("asc","desc").contains(order[1]))) throw new ContentExceptionHandler.Invalid("sort","Unsupported sort",400);
        var cb=support.em.getCriteriaBuilder(); var query=cb.createQuery(entityType); var root=query.from(entityType);
        List<Predicate> predicates=new ArrayList<>();
        if(q!=null&&!q.isBlank()) {
            String escaped=q.trim().toLowerCase(Locale.ROOT).replace("\\","\\\\").replace("%","\\%").replace("_","\\_");
            predicates.add(cb.or(searchable.stream().map(field -> cb.like(cb.lower(root.get(field)),"%"+escaped+"%",'\\')).toArray(Predicate[]::new)));
        }
        query.where(predicates.toArray(Predicate[]::new));
        query.orderBy(order.length==2&&order[1].equals("desc")?cb.desc(root.get(order[0])):cb.asc(root.get(order[0])),cb.asc(root.get("id")));
        var rows=support.em.createQuery(query).setFirstResult(page*size).setMaxResults(size).getResultList().stream().map(mapper::admin).toList();
        var count=cb.createQuery(Long.class);var countRoot=count.from(entityType);count.select(cb.count(countRoot));
        if(q!=null&&!q.isBlank()) {
            String escaped=q.trim().toLowerCase(Locale.ROOT).replace("\\","\\\\").replace("%","\\%").replace("_","\\_");
            count.where(cb.or(searchable.stream().map(field -> cb.like(cb.lower(countRoot.get(field)),"%"+escaped+"%",'\\')).toArray(Predicate[]::new)));
        }
        long total=support.em.createQuery(count).getSingleResult();
        return new PageResponse<>(rows,page,size,total,(int)Math.ceil((double)total/size));
    }
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Transactional public D create(R request) {
        E entity=fresh();mapper.apply(entity,request);support.validate(entity);repository.saveAndFlush(entity);
        D dto=mapper.admin(entity);support.changed(type,entity,"CREATE",Map.of(),dto);support.schedule(entity);return dto;
    }
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Transactional public D update(UUID id,R request) {
        E entity=entity(id);
        if(version(request)==null) throw new ContentExceptionHandler.Invalid("version","Version is required",400);
        if(version(request)!=entity.getVersion()) throw new ObjectOptimisticLockingFailureException(entityType,id);
        var before=support.snapshot(mapper.admin(entity));mapper.apply(entity,request);support.validate(entity);repository.flush();
        D dto=mapper.admin(entity);support.changed(type,entity,"UPDATE",before,dto);support.schedule(entity);return dto;
    }
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Transactional public void delete(UUID id) {
        E entity=entity(id);var before=support.snapshot(mapper.admin(entity));repository.delete(entity);repository.flush();support.changed(type,entity,"DELETE",before,null);
    }
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Transactional public D publish(UUID id,boolean published) {
        E entity=entity(id);var before=support.snapshot(mapper.admin(entity));setPublished(entity,published);support.validate(entity);repository.flush();
        D dto=mapper.admin(entity);support.changed(type,entity,published?"PUBLISH":"UNPUBLISH",before,dto);support.schedule(entity);return dto;
    }
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Transactional public void order(List<UUID> ids) {
        if(ids==null||ids.isEmpty()||new HashSet<>(ids).size()!=ids.size()||ids.stream().anyMatch(Objects::isNull)) throw new ContentExceptionHandler.Invalid("order","Supply unique record IDs",400);
        for(int i=0;i<ids.size();i++) { E entity=entity(ids.get(i));var before=support.snapshot(mapper.admin(entity));setOrder(entity,i);repository.flush();support.changed(type,entity,"REORDER",before,mapper.admin(entity)); }
    }
}
