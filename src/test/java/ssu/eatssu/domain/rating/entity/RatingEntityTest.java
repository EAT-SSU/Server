package ssu.eatssu.domain.rating.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RatingEntityTest {

    @AfterEach
    void resetRatings() {
        ReviewRating.resetAll();
    }

    @Test
    void ratingsAcceptOptionalSubRatingsWithinRange() {
        Ratings ratings = Ratings.of(5, null, 1);

        assertThat(ratings.getMainRating()).isEqualTo(5);
        assertThat(ratings.getAmountRating()).isNull();
        assertThat(ratings.getTasteRating()).isEqualTo(1);
    }

    @Test
    void ratingsRejectMissingOrOutOfRangeMainRating() {
        assertThatThrownBy(() -> Ratings.of(null, 1, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Ratings.of(6, 1, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Ratings.of(1, 0, 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reviewRatingCountsAndResetsEachStar() {
        ReviewRating.fromValue(1).incrementCount();
        ReviewRating.fromValue(5).incrementCount();
        ReviewRating.fromValue(5).incrementCount();

        ReviewRatingCount count = ReviewRating.toResponse();

        assertThat(count).isEqualTo(new ReviewRatingCount(1, 0, 0, 0, 2));
        assertThatThrownBy(() -> ReviewRating.fromValue(0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void ratingCalculatorsReturnNullForMissingAverageInputs() {
        JpaLoadCollectionRatingCalculator loadCalculator = new JpaLoadCollectionRatingCalculator();
        JpaProjectionRatingCalculator projectionCalculator = new JpaProjectionRatingCalculator(null);

        assertThat(loadCalculator.averageRating(null, 1)).isNull();
        assertThat(loadCalculator.averageRating(5, 0)).isNull();
        assertThat(loadCalculator.averageRating(9, 2)).isEqualTo(4.5);
        assertThat(projectionCalculator.averageRating(null, 1)).isNull();
        assertThat(projectionCalculator.averageRating(8, 2)).isEqualTo(4.0);
    }

    @Test
    void ratingCalculatorSumHandlesNullValues() {
        RatingCalculator calculator = new JpaLoadCollectionRatingCalculator();

        assertThat(calculator.sum(null, 3)).isEqualTo(3);
        assertThat(calculator.sum(2, null)).isEqualTo(2);
        assertThat(calculator.sum(2, 3)).isEqualTo(5);
    }
}
