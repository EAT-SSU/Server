package ssu.eatssu.domain.admin.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ssu.eatssu.domain.admin.dto.response.ReportLine;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.report.entity.ReportStatus;
import ssu.eatssu.domain.report.entity.ReportType;
import ssu.eatssu.domain.report.repository.ReportRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LoadReportRepositoryTest {

    @Autowired
    private LoadReportRepository loadReportRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        reportRepository.deleteAll();
        reviewRepository.deleteAll();
        menuRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllReturnsPageOfReportLines() {
        User reporter = userRepository.save(User.create("reporter@test.com", "신고자", OAuthProvider.EATSSU, "1", "1"));
        Menu menu = menuRepository.save(Menu.createFixed("라면", Restaurant.FOOD_COURT, 3000, null));
        Review review = reviewRepository.save(Review.builder().content("리뷰").rating(4).user(reporter).menu(menu).build());
        reportRepository.save(Report.builder().user(reporter).review(review).reportType(ReportType.EXTRA)
                                    .content("신고").status(ReportStatus.PENDING).build());

        Page<ReportLine> page = loadReportRepository.findAll(PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).extracting(ReportLine::reviewText).containsExactly("리뷰");
    }

    @Test
    void findAllByReviewIdReturnsReportIdsForThatReview() {
        User reporter = userRepository.save(User.create("reporter@test.com", "신고자", OAuthProvider.EATSSU, "1", "1"));
        Menu menu = menuRepository.save(Menu.createFixed("라면", Restaurant.FOOD_COURT, 3000, null));
        Review review = reviewRepository.save(Review.builder().content("리뷰").rating(4).user(reporter).menu(menu).build());
        Report report = reportRepository.save(Report.builder().user(reporter).review(review).reportType(ReportType.EXTRA)
                                                     .content("신고").status(ReportStatus.PENDING).build());

        List<Long> reportIds = loadReportRepository.findAllByReviewId(review.getId());

        assertThat(reportIds).containsExactly(report.getId());
    }
}
