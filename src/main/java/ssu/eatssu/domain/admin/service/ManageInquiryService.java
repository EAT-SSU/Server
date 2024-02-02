package ssu.eatssu.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.admin.dto.InquiryLine;
import ssu.eatssu.domain.admin.dto.PageWrapper;
import ssu.eatssu.domain.admin.dto.UpdateStatusRequest;
import ssu.eatssu.domain.admin.persistence.LoadInquiryRepository;
import ssu.eatssu.domain.admin.persistence.ManageInquiryRepository;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

@Service
@RequiredArgsConstructor
public class ManageInquiryService {

    private final LoadInquiryRepository loadInquiryRepository;
    private final ManageInquiryRepository manageInquiryRepository;

    public PageWrapper<InquiryLine> getInquiryBoard(Pageable pageable) {
        Page<Inquiry> inquiries = loadInquiryRepository.findAllInquiries(pageable);
        return new PageWrapper<>(inquiries.map(InquiryLine::new));
    }

    public void updateStatus(Long inquiryId, UpdateStatusRequest request) {
        Inquiry inquiry = manageInquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER_INQUIRY));
        inquiry.updateStatus(request.status());
        manageInquiryRepository.save(inquiry);
    }

    public void delete(Long inquiryId) {
        manageInquiryRepository.deleteById(inquiryId);
    }
}
