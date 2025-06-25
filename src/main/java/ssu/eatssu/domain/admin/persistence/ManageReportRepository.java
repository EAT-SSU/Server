package ssu.eatssu.domain.admin.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.review.entity.Report;

public interface ManageReportRepository extends JpaRepository<Report, Long> {
}
