package za.co.heritagebaptist.forge.content.sermon;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SermonSeriesRepository extends JpaRepository<SermonSeries, UUID> {
}