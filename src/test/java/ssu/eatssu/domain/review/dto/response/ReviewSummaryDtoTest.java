package ssu.eatssu.domain.review.dto.response;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.review.dto.RatingAverages;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.rating.entity.Ratings;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ReviewSummaryDtoTest {

    private final RatingAverages averages = new RatingAverages(4.5);
    private final ReviewRatingCount count = new ReviewRatingCount(1, 2, 3, 4, 5);

    @Test
    void summaryResponsesMapRatingCountsAndMenuProperties() {
        Menu menu = mock(Menu.class);
        given(menu.getName()).willReturn("돈가스");
        given(menu.getTotalReviewCount()).willReturn(3);
        given(menu.getLikeCount()).willReturn(7);

        assertThat(MealReviewsResponse.of(3L, List.of("돈가스"), averages, count).getMainRating()).isEqualTo(4.5);
        assertThat(MealReviewsV2Response.of(3L, List.of(), averages, count).getRating()).isEqualTo(4.5);
        assertThat(MenuReviewResponse.of(menu, averages, count).getMenuName()).isEqualTo("돈가스");
        assertThat(MenuReviewsV2Response.of(3L, "돈가스", averages, count, menu).getLikeCount()).isEqualTo(7);
    }

    @Test
    void reviewRatingCountUsesEmbeddedRatingThenLegacyRating() {
        Review embedded = Review.builder().ratings(Ratings.of(5, null, null)).build();
        Review legacy = Review.builder().rating(2).build();
        Review missing = Review.builder().build();

        assertThat(ReviewRatingCount.from(List.of(embedded, legacy, missing)))
                .isEqualTo(new ReviewRatingCount(0, 1, 0, 0, 1));
    }
}
