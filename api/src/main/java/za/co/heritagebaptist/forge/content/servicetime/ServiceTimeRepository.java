package za.co.heritagebaptist.forge.content.servicetime;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceTimeRepository extends JpaRepository<ServiceTime, UUID> {
}