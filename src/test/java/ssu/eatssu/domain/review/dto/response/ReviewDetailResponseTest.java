package ssu.eatssu.domain.review.dto.response;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewImage;
import ssu.eatssu.domain.review.entity.ReviewMenuLike;
import ssu.eatssu.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ReviewDetailResponseTest {

    @Test
    void reviewDetailMapsMenuLikeAndWriterStatus() {
        Menu menu = mock(Menu.class);
        given(menu.getId()).willReturn(2L);
        given(menu.getName()).willReturn("돈가스");
        User user = mock(User.class);
        given(user.getId()).willReturn(7L);
        given(user.getNickname()).willReturn("먹방러");
        ReviewMenuLike like = mock(ReviewMenuLike.class);
        given(like.getIsLike()).willReturn(true);
        given(like.getMenu()).willReturn(menu);
        ReviewImage image = mock(ReviewImage.class);
        given(image.getImageUrl()).willReturn("https://cdn.example/review.jpg");
        Review review = Review.builder().id(3L).menu(menu).user(user).content("맛있어요")
                .rating(5).reviewImages(List.of(image)).menuLikes(List.of(like)).build();
        ReflectionTestUtils.setField(review, "createdDate", LocalDateTime.of(2026, 1, 2, 3, 4));

        ReviewDetail response = ReviewDetail.from(review, 7L);

        assertThat(response.getReviewId()).isEqualTo(3L);
        assertThat(response.getMenu()).isEqualTo(new MenuIdNameLikeDto(2L, "돈가스", true));
        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getWrittenAt()).isEqualTo(java.time.LocalDate.of(2026, 1, 2));
        assertThat(response.getWriterNickname()).isEqualTo("먹방러");
        assertThat(response.getIsWriter()).isTrue();
        assertThat(response.getContent()).isEqualTo("맛있어요");
        assertThat(response.getImageUrls()).containsExactly("https://cdn.example/review.jpg");
    }

    @Test
    void mealReviewResponseHandlesMealAndAnonymousWriter() {
        Review review = mock(Review.class);
        Meal meal = mock(Meal.class);
        given(review.getId()).willReturn(4L);
        given(review.getMeal()).willReturn(meal);
        given(review.getMenu()).willReturn(null);
        given(review.getUser()).willReturn(null);
        given(review.getRating()).willReturn(null);
        given(review.getRatings()).willReturn(Ratings.of(4, null, null));
        given(review.getCreatedDate()).willReturn(LocalDateTime.of(2026, 2, 3, 4, 5));
        given(review.getReviewImages()).willReturn(List.of());
        given(review.getMenuLikes()).willReturn(List.of());

        MealReviewResponse response = MealReviewResponse.from(review, 99L, List.of(), 4);

        assertThat(response.getReviewId()).isEqualTo(4L);
        assertThat(response.getRating()).isEqualTo(4);
        assertThat(response.getWriterNickname()).isEqualTo("알 수 없음");
        assertThat(response.getIsWriter()).isFalse();
        assertThat(response.getMenuList()).isEmpty();
    }

    @Test
    void mealReviewResponseHandlesMenuOnlyReviewAndOtherWriter() {
        Menu menu = mock(Menu.class);
        given(menu.getId()).willReturn(2L);
        given(menu.getName()).willReturn("돈가스");
        User writer = mock(User.class);
        given(writer.getId()).willReturn(7L);
        given(writer.getNickname()).willReturn("먹방러");
        Review review = Review.builder().id(5L).menu(menu).user(writer).rating(4)
                .reviewImages(List.of()).menuLikes(List.of()).build();
        ReflectionTestUtils.setField(review, "createdDate", LocalDateTime.of(2026, 3, 4, 5, 6));

        MealReviewResponse response = MealReviewResponse.from(review, 99L, List.of(), 4);

        assertThat(response.getMenuList()).containsExactly(new MenuIdNameLikeDto(2L, "돈가스", false));
        assertThat(response.getIsWriter()).isFalse();
        assertThat(response.getWriterNickname()).isEqualTo("먹방러");
    }

    @Test
    void mealReviewResponseMarksSelfAsWriter() {
        Review review = mock(Review.class);
        Meal meal = mock(Meal.class);
        User writer = mock(User.class);
        given(writer.getId()).willReturn(7L);
        given(writer.getNickname()).willReturn("먹방러");
        given(review.getId()).willReturn(6L);
        given(review.getMeal()).willReturn(meal);
        given(review.getUser()).willReturn(writer);
        given(review.getRating()).willReturn(5);
        given(review.getCreatedDate()).willReturn(LocalDateTime.of(2026, 4, 5, 6, 7));
        given(review.getReviewImages()).willReturn(List.of());
        given(review.getMenuLikes()).willReturn(List.of());

        MealReviewResponse response = MealReviewResponse.from(review, 7L, List.of(), 5);

        assertThat(response.getIsWriter()).isTrue();
        assertThat(response.getWriterId()).isEqualTo(7L);
    }

    @Test
    void reviewDetailHandlesAnonymousAndOtherWriter() {
        Menu menu = mock(Menu.class);
        given(menu.getId()).willReturn(2L);
        given(menu.getName()).willReturn("돈가스");
        Review anonymous = Review.builder().id(8L).menu(menu).rating(4)
                .reviewImages(List.of()).menuLikes(List.of()).build();
        ReflectionTestUtils.setField(anonymous, "createdDate", LocalDateTime.of(2026, 5, 1, 0, 0));

        ReviewDetail anonymousResponse = ReviewDetail.from(anonymous, 99L);

        assertThat(anonymousResponse.getWriterNickname()).isEqualTo("알 수 없음");
        assertThat(anonymousResponse.getIsWriter()).isFalse();

        User writer = mock(User.class);
        given(writer.getId()).willReturn(7L);
        given(writer.getNickname()).willReturn("먹방러");
        Review otherWriter = Review.builder().id(9L).menu(menu).user(writer).rating(4)
                .reviewImages(List.of()).menuLikes(List.of()).build();
        ReflectionTestUtils.setField(otherWriter, "createdDate", LocalDateTime.of(2026, 5, 1, 0, 0));

        ReviewDetail otherResponse = ReviewDetail.from(otherWriter, 99L);

        assertThat(otherResponse.getWriterNickname()).isEqualTo("먹방러");
        assertThat(otherResponse.getIsWriter()).isFalse();
    }
}
