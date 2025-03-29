package ssu.eatssu.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ssu.eatssu.domain.review.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
