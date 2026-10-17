package za.co.heritagebaptist.forge.issue;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueReportRepository extends JpaRepository<IssueReport, UUID> {
}