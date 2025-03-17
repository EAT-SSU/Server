package ssu.eatssu.domain.admin.dto;

import java.time.LocalDateTime;

import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.inquiry.entity.InquiryStatus;

public record InquiryLine(Long inquiryId, LocalDateTime date, String email, String content, InquiryStatus status) {
	public InquiryLine(Inquiry inquiry) {
		this(inquiry.getId(), inquiry.getCreatedDate(), inquiry.getEmail(), inquiry.getContent(), inquiry.getStatus());
	}
}
