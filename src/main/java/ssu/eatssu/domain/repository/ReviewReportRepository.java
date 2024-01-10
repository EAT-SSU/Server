package ssu.eatssu.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.review.ReviewReport;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long>{
}
