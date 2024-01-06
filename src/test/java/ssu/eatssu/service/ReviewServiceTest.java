package ssu.eatssu.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.enums.OauthProvider;

@SpringBootTest
@RequiredArgsConstructor
class ReviewServiceTest {

    private final ReviewService reviewService;

    @Test
    void 리뷰를_생성한다() {
        // given
        String email = "test@test.com";
        String prodivderId = "1";
        User user = User.oAuthJoin(email, OauthProvider.EATSSU, prodivderId);

        // when

        // then
    }
}