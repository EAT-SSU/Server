package ssu.eatssu.domain.rating.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.review.dto.RatingAverages;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RatingEntityTest {

    private static final RatingCalculator EMPTY_CALCULATOR = new RatingCalculator() {
        @Override
        public ReviewRatingCount mealRatingCount(Meal meal) {
            return null;
        }

        @Override
        public ReviewRatingCount menuRatingCount(Menu menu) {
            return null;
        }

        @Override
        public RatingAverages mealAverageRatings(Meal meal) {
            return null;
        }

        @Override
        public RatingAverages menuAverageRatings(Menu menu) {
            return null;
        }

        @Override
        public Double mealAverageMainRating(Meal meal) {
            return null;
        }

        @Override
        public Double menuAverageMainRating(Menu menu) {
            return null;
        }

        @Override
        public long mealTotalReviewCount(Meal meal) {
            return 0;
        }
    };

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
    void ratingCalculatorSumHandlesNullValues() {
        assertThat(EMPTY_CALCULATOR.sum(null, 3)).isEqualTo(3);
        assertThat(EMPTY_CALCULATOR.sum(2, null)).isEqualTo(2);
        assertThat(EMPTY_CALCULATOR.sum(2, 3)).isEqualTo(5);
    }
}
