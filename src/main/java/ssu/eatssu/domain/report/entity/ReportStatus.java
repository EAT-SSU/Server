package ssu.eatssu.domain.report.entity;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public enum ReportStatus {
	PENDING("대기 중"),
	IN_PROGRESS("진행 중"),
	RESOLVED("해결됨"),
	REJECTED("거절됨"),
	FALSE_REPORT("거짓 신고");

	private final String description;

	ReportStatus(final String description) {
		this.description = description;
	}

	@JsonCreator
	public static ReportStatus from(final String description) {
		return Arrays.stream(ReportStatus.values())
					 .filter(v -> v.getDescription().equals(description))
					 .findAny()
					 .orElseThrow(IllegalArgumentException::new);
	}
}
