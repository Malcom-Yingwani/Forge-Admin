package za.co.heritagebaptist.forge.content.growthgroup;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrowthGroupRepository extends JpaRepository<GrowthGroup, UUID> {
}