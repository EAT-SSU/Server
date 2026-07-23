package ssu.eatssu.domain.admin.dto.request;

import ssu.eatssu.domain.inquiry.entity.InquiryStatus;

public record UpdateStatusRequest(InquiryStatus status) {
}
