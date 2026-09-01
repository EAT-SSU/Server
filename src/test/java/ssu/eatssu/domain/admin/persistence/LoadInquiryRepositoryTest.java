package ssu.eatssu.domain.admin.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.inquiry.entity.InquiryStatus;
import ssu.eatssu.domain.inquiry.repository.InquiryRepository;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LoadInquiryRepositoryTest {

    @Autowired
    private LoadInquiryRepository loadInquiryRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        inquiryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        inquiryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllInquiriesOrdersWaitingBeforeAnsweredRegardlessOfDate() {
        User user = userRepository.save(User.create("test@test.com", "user-test", OAuthProvider.EATSSU, "1234", "1234"));
        Inquiry answered = inquiryRepository.save(new Inquiry("답변완료 문의", user, "test@test.com"));
        answered.updateStatus(InquiryStatus.ANSWERED);
        inquiryRepository.save(answered);
        Inquiry waiting = inquiryRepository.save(new Inquiry("대기중 문의", user, "test@test.com"));

        Page<Inquiry> page = loadInquiryRepository.findAllInquiries(PageRequest.of(0, 10));

        assertThat(page.getContent()).extracting(Inquiry::getId).containsExactly(waiting.getId(), answered.getId());
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findAllInquiriesRunsCountQueryWhenPageIsFull() {
        User user = userRepository.save(User.create("test@test.com", "user-test", OAuthProvider.EATSSU, "1234", "1234"));
        inquiryRepository.save(new Inquiry("문의1", user, "test@test.com"));
        inquiryRepository.save(new Inquiry("문의2", user, "test@test.com"));

        // 페이지 크기와 조회된 개수가 같으면 PageableExecutionUtils가 실제 count 쿼리를 실행한다
        Page<Inquiry> page = loadInquiryRepository.findAllInquiries(PageRequest.of(0, 1));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
    }
}
