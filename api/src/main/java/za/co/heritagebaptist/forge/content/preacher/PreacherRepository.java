package za.co.heritagebaptist.forge.content.preacher;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreacherRepository extends JpaRepository<Preacher, UUID> {
}