package za.co.heritagebaptist.forge.content.giving;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GivingFundRepository extends JpaRepository<GivingFund, UUID> {
}