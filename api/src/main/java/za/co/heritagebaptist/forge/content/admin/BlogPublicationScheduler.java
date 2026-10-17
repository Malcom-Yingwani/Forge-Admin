package za.co.heritagebaptist.forge.content.admin;
import java.util.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import za.co.heritagebaptist.forge.content.blog.BlogPost;
import za.co.heritagebaptist.forge.sync.SyncHook;
@Component
public class BlogPublicationScheduler {
    private final ContentSupport support;
    private final ObjectProvider<SyncHook> hook;
    public BlogPublicationScheduler(ContentSupport support,ObjectProvider<SyncHook> hook) { this.support=support;this.hook=hook; }
    @Scheduled(fixedDelay=30000,initialDelay=30000)
    @Transactional
    public void publishDue() {
        @SuppressWarnings("unchecked")
        List<UUID> due=support.em.createNativeQuery("select blog_id from scheduled_publication where scheduled_at<=now() order by scheduled_at limit 50",UUID.class).getResultList();
        for(UUID id:due) {
            var blog=support.em.find(BlogPost.class,id,jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
            var claimed=support.em.createNativeQuery("select 1 from scheduled_publication where blog_id=:id and scheduled_at<=now() for update skip locked",Integer.class).setParameter("id",id).getResultList();
            if(claimed.isEmpty()) continue;
            if(blog!=null&&blog.isPublished()&&blog.getPublishedAt()!=null&&!blog.getPublishedAt().isAfter(java.time.Instant.now())) hook.getObject().onChanged(blog);
            support.em.createNativeQuery("delete from scheduled_publication where blog_id=:id").setParameter("id",id).executeUpdate();
        }
    }
}
