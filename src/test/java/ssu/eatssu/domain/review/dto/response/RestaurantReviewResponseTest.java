package ssu.eatssu.domain.review.dto.response;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantReviewResponseTest {

    @Test
    void builderStoresReviewSummaryValues() {
        ReviewRatingCount counts = new ReviewRatingCount(1, 2, 3, 4, 5);

        RestaurantReviewResponse response = RestaurantReviewResponse.builder()
                .totalReviewCount(15).reviewRatingCount(counts).rating(4.4)
                .likeCount(12).unlikeCount(3).build();

        assertThat(response.getTotalReviewCount()).isEqualTo(15);
        assertThat(response.getReviewRatingCount()).isSameAs(counts);
        assertThat(response.getRating()).isEqualTo(4.4);
        assertThat(response.getLikeCount()).isEqualTo(12);
        assertThat(response.getUnlikeCount()).isEqualTo(3);
    }
}
