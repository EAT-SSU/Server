package ssu.eatssu.domain.admin.dto;

import ssu.eatssu.domain.inquiry.entity.InquiryStatus;

public record UpdateStatusRequest(InquiryStatus status) {
}
