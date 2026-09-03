package ssu.eatssu.domain.slack.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.inquiry.repository.InquiryRepository;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.menu.persistence.MealRepository;
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
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SlackMessageFormatTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(SlackMessageFormat.class, "serverEnv", "test");
        ReflectionTestUtils.setField(SlackMessageFormat.class, "grafanaBaseUrl", "");
        ReflectionTestUtils.setField(SlackMessageFormat.class, "lokiDatasourceUid", "");
        MDC.remove("requestId");
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        ReflectionTestUtils.setField(SlackMessageFormat.class, "grafanaBaseUrl", "");
        ReflectionTestUtils.setField(SlackMessageFormat.class, "lokiDatasourceUid", "");
        MDC.remove("requestId");
        cleanUp();
    }

    private void cleanUp() {
        reportRepository.deleteAll();
        inquiryRepository.deleteAll();
        reviewRepository.deleteAll();
        mealRepository.deleteAll();
        menuRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void sendServerErrorFormatsBaseExceptionStatus() {
        String message = SlackMessageFormat.sendServerError(new BaseException(BaseResponseStatus.NOT_FOUND_USER),
                "GET", "/users/me", "1", null);

        assertThat(message).contains("예외 상태코드", "해당 회원을 찾을 수 없습니다.", "개발환경: test");
    }

    @Test
    void sendServerErrorFormatsRegularExceptionAndTruncatesLongArguments() {
        String message = SlackMessageFormat.sendServerError(new IllegalStateException(), "POST", "/reviews", "1",
                "x".repeat(501));

        assertThat(message).contains("예외 타입", "IllegalStateException", "메시지 없음", "...(truncated)");
    }

    @Test
    void sendServerErrorOmitsGrafanaLinkWhenNotConfigured() {
        // given: grafana.base-url is left unconfigured (default test setup)

        // when
        String message = SlackMessageFormat.sendServerError(new IllegalStateException(), "GET", "/menus", "1", null);

        // then
        assertThat(message).doesNotContain("Grafana에서 로그 보기");
    }

    @Test
    void sendServerErrorIncludesGrafanaLogLinkWhenConfiguredAndRequestIdPresent() {
        // given
        ReflectionTestUtils.setField(SlackMessageFormat.class, "grafanaBaseUrl", "https://example.grafana.net");
        ReflectionTestUtils.setField(SlackMessageFormat.class, "lokiDatasourceUid", "grafanacloud-logs");
        MDC.put("requestId", "test-request-id");

        // when
        String message = SlackMessageFormat.sendServerError(new IllegalStateException(), "GET", "/menus", "1", null);

        // then
        assertThat(message).contains("https://example.grafana.net/explore?schemaVersion=1&panes=",
                                      "Grafana에서 로그 보기");
    }

    @Test
    void slackChannelsExposeConfiguredAddress() {
        assertThat(SlackChannel.SERVER_ERROR.getKrName()).isEqualTo("C092J4J6F0U");
    }

    @Test
    void sendReportFormatsReportOnMenuReview() {
        User reporter = createUser("reporter@test.com", "신고자");
        User writer = createUser("writer@test.com", "작성자");
        Menu menu = menuRepository.save(Menu.createFixed("라면", Restaurant.FOOD_COURT, 3000, null));
        Review review = reviewRepository.save(Review.builder()
                                                     .content("리뷰내용")
                                                     .rating(4)
                                                     .user(writer)
                                                     .menu(menu)
                                                     .build());
        Report report = reportRepository.save(Report.builder()
                                                     .user(reporter)
                                                     .review(review)
                                                     .reportType(ReportType.IMPROPER_CONTENT)
                                                     .content("신고내용")
                                                     .status(ReportStatus.PENDING)
                                                     .build());

        String message = SlackMessageFormat.sendReport(report);

        assertThat(message).contains("신고자", "작성자", "라면", "리뷰내용", "신고내용",
                                      ReportType.IMPROPER_CONTENT.getDescription());
    }

    @Test
    void sendReportFormatsReportOnMealReview() {
        User reporter = createUser("reporter@test.com", "신고자");
        User writer = createUser("writer@test.com", "작성자");
        Menu menu = menuRepository.save(Menu.createVariable("돈까스", Restaurant.DODAM));
        Meal meal = mealRepository.save(new Meal(new Date(), TimePart.LUNCH, Restaurant.DODAM));
        Review review = reviewRepository.save(Review.builder()
                                                     .content("식단리뷰")
                                                     .rating(5)
                                                     .user(writer)
                                                     .meal(meal)
                                                     .build());
        Report report = reportRepository.save(Report.builder()
                                                     .user(reporter)
                                                     .review(review)
                                                     .reportType(ReportType.COPY)
                                                     .content("신고내용")
                                                     .status(ReportStatus.PENDING)
                                                     .build());

        String message = SlackMessageFormat.sendReport(report);

        assertThat(message).contains("신고자", "작성자", "식단리뷰", String.valueOf(meal.getId()));
    }

    @Test
    void sendUserInquiryFormatsInquiry() {
        User user = createUser("inquiry@test.com", "문의자");
        Inquiry inquiry = inquiryRepository.save(new Inquiry("문의내용", user, "inquiry@test.com"));

        String message = SlackMessageFormat.sendUserInquiry(inquiry);

        assertThat(message).contains("문의자", "inquiry@test.com", "문의내용");
    }

    private User createUser(String email, String nickname) {
        return userRepository.save(User.create(email, nickname, OAuthProvider.EATSSU, "1234", "1234"));
    }
}
