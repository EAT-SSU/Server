package ssu.eatssu.domain.admin.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ssu.eatssu.domain.admin.dto.PageWrapper;
import ssu.eatssu.domain.admin.dto.ReportLine;
import ssu.eatssu.domain.admin.persistence.LoadReportRepository;
import ssu.eatssu.domain.admin.persistence.ManageReportRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageReportService {

	private final LoadReportRepository loadReportRepository;
	private final ManageReportRepository manageReportRepository;

	public PageWrapper<ReportLine> getReportBoard(Pageable pageable) {
		Page<ReportLine> reports = loadReportRepository.findAll(pageable);
		return new PageWrapper<>(reports);
	}

	public void delete(Long reportId) {
		manageReportRepository.deleteById(reportId);
	}

	public void deleteAllByReviewId(Long reviewId) {
		List<Long> reportIds = loadReportRepository.findAllByReviewId(reviewId);
		manageReportRepository.deleteAllByIdInBatch(reportIds);
	}
}
