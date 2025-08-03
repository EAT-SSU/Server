package ssu.eatssu.domain.inquiry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.inquiry.entity.Inquiry;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}
