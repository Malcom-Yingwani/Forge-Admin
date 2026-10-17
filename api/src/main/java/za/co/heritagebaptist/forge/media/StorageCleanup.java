package za.co.heritagebaptist.forge.media;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
@Service
public class StorageCleanup {
    private final JdbcTemplate jdbc;
    private final StorageService storage;
    public StorageCleanup(JdbcTemplate jdbc,StorageService storage) { this.jdbc=jdbc;this.storage=storage; }
    @Transactional public void enqueue(Collection<String> keys) { for(String key:keys) jdbc.update("insert into media_cleanup(storage_key) values (?) on conflict do nothing",key); }
    @Transactional(propagation=Propagation.REQUIRES_NEW) public void enqueueOrphans(Collection<String> keys) { enqueue(keys); }
    @Scheduled(fixedDelay=10000,initialDelay=10000)
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void run() {
        var keys=jdbc.queryForList("select storage_key from media_cleanup where next_attempt_at<=now() order by next_attempt_at for update skip locked limit 10",String.class);
        for(String key:keys) {
            try { storage.delete(key);jdbc.update("delete from media_cleanup where storage_key=?",key); }
            catch(java.io.IOException|RuntimeException exception) { jdbc.update("update media_cleanup set attempts=attempts+1,next_attempt_at=now()+interval '1 minute' where storage_key=?",key); }
        }
    }
}
