package ssu.eatssu.domain.review.dto.response;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewImage;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ReviewDetailV1Test {

    @Test
    void mapsRatingsMenuImagesAndWriter() {
        Menu menu = Menu.createVariable("돈가스", Restaurant.DODAM);
        User user = mock(User.class);
        given(user.getId()).willReturn(7L);
        given(user.getNickname()).willReturn("먹방러");
        ReviewImage image = mock(ReviewImage.class);
        given(image.getImageUrl()).willReturn("https://cdn.example/review.jpg");
        Review review = Review.builder().id(3L).menu(menu).user(user).ratings(Ratings.of(5, 4, 3))
                .content("맛있어요").reviewImages(List.of(image)).build();
        ReflectionTestUtils.setField(review, "createdDate", LocalDateTime.of(2026, 9, 1, 10, 0));

        ReviewDetailV1 response = ReviewDetailV1.from(review, 7L);

        assertThat(response.getReviewId()).isEqualTo(3L);
        assertThat(response.getMenu()).isEqualTo("돈가스");
        assertThat(response.getMainRating()).isEqualTo(5);
        assertThat(response.getAmountRating()).isEqualTo(4);
        assertThat(response.getTasteRating()).isEqualTo(3);
        assertThat(response.getWritedAt()).isEqualTo(LocalDate.of(2026, 9, 1));
        assertThat(response.getWriterNickname()).isEqualTo("먹방러");
        assertThat(response.getIsWriter()).isTrue();
        assertThat(response.getContent()).isEqualTo("맛있어요");
        assertThat(response.getImageUrls()).containsExactly("https://cdn.example/review.jpg");
    }

    @Test
    void marksAnonymousWriter() {
        Menu menu = Menu.createVariable("샐러드", Restaurant.DODAM);
        Review review = Review.builder().menu(menu).ratings(Ratings.of(2, null, null)).reviewImages(List.of()).build();
        ReflectionTestUtils.setField(review, "createdDate", LocalDateTime.of(2026, 9, 1, 10, 0));

        ReviewDetailV1 response = ReviewDetailV1.from(review, 1L);

        assertThat(response.getWriterId()).isNull();
        assertThat(response.getWriterNickname()).isEqualTo("알 수 없음");
        assertThat(response.getIsWriter()).isFalse();
    }
}
