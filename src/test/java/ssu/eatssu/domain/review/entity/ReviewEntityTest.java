package ssu.eatssu.domain.review.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.rating.entity.ReviewRating;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewEntityTest {

    @AfterEach
    void resetRatings() {
        ReviewRating.resetAll();
    }

    @Test
    void updateChangesContentAndRatings() {
        Review review = Review.builder().content("before").build();

        review.update("after", 5, null, 3);

        assertThat(review.getContent()).isEqualTo("after");
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getRatings().getTasteRating()).isEqualTo(3);
    }

    @Test
    void menuLikeAddsLikeAndResetCancelsIt() {
        Menu menu = Menu.createVariable("메뉴", Restaurant.DODAM);
        Review review = Review.builder().build();

        review.addReviewMenuLike(menu, true);
        review.addReviewMenuLike(menu, false);

        assertThat(menu.getLikeCount()).isEqualTo(1);
        assertThat(review.getMenuLikes()).hasSize(2);

        review.resetMenuLikes();

        assertThat(menu.getLikeCount()).isZero();
        assertThat(review.getMenuLikes()).isEmpty();
    }

    @Test
    void reviewsIgnoresMissingRatingsWhenAggregating() {
        Reviews reviews = new Reviews();
        reviews.add(Review.builder().ratings(Ratings.of(2, null, null)).build());
        reviews.add(Review.builder().ratings(Ratings.of(5, 1, 1)).build());
        reviews.add(Review.builder().build());

        reviews.calculateReviewRatings();

        assertThat(reviews.size()).isEqualTo(3);
        assertThat(reviews.getTotalMainRating()).isEqualTo(7);
        assertThat(ReviewRating.toResponse().twoStarCount()).isEqualTo(1);
        assertThat(ReviewRating.toResponse().fiveStarCount()).isEqualTo(1);
    }
}
