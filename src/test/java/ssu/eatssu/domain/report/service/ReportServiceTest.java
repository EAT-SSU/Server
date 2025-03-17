package ssu.eatssu.domain.report.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.report.dto.ReportCreateRequest;
import ssu.eatssu.domain.report.entity.ReportType;
import ssu.eatssu.domain.report.repository.ReportRepository;
import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

@SpringBootTest
class ReportServiceTest {

	@Autowired
	private ReportService reportService;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private ReportRepository reportRepository;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		reportRepository.deleteAll();
		reviewRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	@Transactional
	void 신고를_생성한다() {
		//given
		User user = createUser(); // 이 부분에서 User가 데이터베이스에 저장되었는지 확인
		Review review = createReview(user);
		CustomUserDetails userDetails = createCustomUserDetails(user);
		ReportCreateRequest request = new ReportCreateRequest(
			review.getId(), ReportType.IMPROPER_ADVERTISEMENT, "음란성, 욕설 등 부적절한 내용");

		//when
		Report createdReport = reportService.reportReview(userDetails, request);

		//then
		Assertions.assertThat(reportRepository.findAll()).hasSize(1);
		Assertions.assertThat(createdReport.getContent())
				  .isEqualTo("음란성, 욕설 등 부적절한 내용");
	}

	private User createUser() {
		return User.create("test1@test.com", "user-test", OAuthProvider.EATSSU, "1234", "1234");
	}

	private CustomUserDetails createCustomUserDetails(User user) {
		User savedUser = userRepository.save(user);
		return new CustomUserDetails(savedUser);
	}

	private Review createReview(User user) {
		return reviewRepository.save(Review.builder()
										   .user(user)
										   .content("굿")
										   .build());
	}

}