package za.co.heritagebaptist.forge.sync;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncOutboxRepository extends JpaRepository<SyncOutbox, UUID> {
}