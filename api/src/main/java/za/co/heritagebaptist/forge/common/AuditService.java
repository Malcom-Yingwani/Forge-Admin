package za.co.heritagebaptist.forge.common;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import za.co.heritagebaptist.forge.auth.AppUser;

@Service
public class AuditService {
    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public AuditLog record(AppUser actor, String action, String entityType, UUID entityId, Map<String, Object> diff) {
        AuditLog log = new AuditLog();
        log.setActor(actor);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setAt(Instant.now());
        log.setDiff(diff);
        return repository.save(log);
    }
}
