package za.co.heritagebaptist.forge.content.ministry;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MinistryRepository extends JpaRepository<Ministry, UUID> {
}