package ssu.eatssu.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssu.eatssu.domain.review.entity.Report;

import java.time.LocalDateTime;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("""
    SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
    FROM Report r
    WHERE r.user.id = :userId
      AND r.review.id = :reviewId
      AND r.createdDate >= CURRENT_TIMESTAMP - 1
""")
    boolean existsRecentReport(@Param("userId") Long userId,
                               @Param("reviewId") Long reviewId);
}
