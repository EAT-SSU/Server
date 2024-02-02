package ssu.eatssu.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.admin.dto.InquiryLine;
import ssu.eatssu.domain.admin.dto.PageWrapper;
import ssu.eatssu.domain.admin.persistence.LoadInquiryRepository;
import ssu.eatssu.domain.inquiry.entity.Inquiry;

@Service
@RequiredArgsConstructor
public class ManageInquiryService {

    private final LoadInquiryRepository loadInquiryRepository;

    public PageWrapper<InquiryLine> getInquiryBoard(Pageable pageable) {
        Page<Inquiry> inquiries = loadInquiryRepository.findAllInquiries(pageable);
        return new PageWrapper<>(inquiries.map(InquiryLine::new));
    }
}
