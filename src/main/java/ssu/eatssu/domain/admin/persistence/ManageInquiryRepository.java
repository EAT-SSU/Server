package ssu.eatssu.domain.admin.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.inquiry.entity.Inquiry;

public interface ManageInquiryRepository extends JpaRepository<Inquiry, Long> {
}
