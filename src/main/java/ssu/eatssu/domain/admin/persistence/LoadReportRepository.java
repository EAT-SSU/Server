package ssu.eatssu.domain.admin.persistence;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import ssu.eatssu.domain.admin.dto.ReportLine;
import ssu.eatssu.domain.review.entity.QReport;
import ssu.eatssu.domain.review.entity.QReview;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LoadReportRepository {
	private final JPAQueryFactory queryFactory;
	private final QReport report = QReport.report;
	private final QReview review = QReview.review;

	public Page<ReportLine> findAll(Pageable pageable) {
		return PageableExecutionUtils.getPage(fetchReportLines(pageable), pageable, () -> countReports().fetchOne());
	}

	private JPAQuery<Long> countReports() {
		return queryFactory
			.select(report.count())
			.from(report);
	}

	private List<ReportLine> fetchReportLines(Pageable pageable) {
		return queryFactory
			.select(Projections.constructor(ReportLine.class, report, review))
			.from(report)
			.join(report.review, review)
			.orderBy(report.createdDate.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	public List<Long> findAllByReviewId(Long reviewId) {
		return queryFactory
			.select(report.id)
			.from(report)
			.where(report.review.id.eq(reviewId))
			.fetch();
	}
}
