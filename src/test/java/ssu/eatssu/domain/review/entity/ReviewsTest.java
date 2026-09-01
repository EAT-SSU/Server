package ssu.eatssu.domain.review.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.rating.entity.ReviewRating;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewsTest {

    @AfterEach
    void resetRatings() {
        ReviewRating.resetAll();
    }

    @Test
    void addIncreasesSize() {
        Reviews reviews = new Reviews();

        reviews.add(Review.builder().build());
        reviews.add(Review.builder().build());

        assertThat(reviews.size()).isEqualTo(2);
    }

    @Test
    void emptyReviewsHaveZeroTotalMainRating() {
        Reviews reviews = new Reviews();

        assertThat(reviews.size()).isZero();
        assertThat(reviews.getTotalMainRating()).isZero();
    }

    @Test
    void calculateReviewRatingsSkipsReviewsWithoutRatings() {
        Reviews reviews = new Reviews();
        reviews.add(Review.builder().ratings(Ratings.of(4, null, null)).build());
        reviews.add(Review.builder().build());

        reviews.calculateReviewRatings();

        assertThat(ReviewRating.toResponse().fourStarCount()).isEqualTo(1);
        assertThat(ReviewRating.toResponse().oneStarCount()).isZero();
    }
}
