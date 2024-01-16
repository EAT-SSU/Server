package ssu.eatssu.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.review.entity.ReviewReport;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long>{
}
