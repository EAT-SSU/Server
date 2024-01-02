package ssu.eatssu.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.UserInquiry;

public interface UserInquiryRepository extends JpaRepository<UserInquiry, Long>{
}
