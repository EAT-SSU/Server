package ssu.eatssu.domain.review.entity;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ReviewSupportEntityTest {

    @Test
    void createsReviewImageAndLikeRelations() {
        Review review = Review.builder().build();
        User user = mock(User.class);
        Menu menu = Menu.createVariable("돈가스", Restaurant.DODAM);

        ReviewImage image = new ReviewImage(review, "image.jpg");
        ReviewLike like = ReviewLike.create(user, review);
        ReviewMenuLike menuLike = ReviewMenuLike.create(review, menu, true);

        assertThat(image.getReview()).isSameAs(review);
        assertThat(image.getImageUrl()).isEqualTo("image.jpg");
        assertThat(like.getUser()).isSameAs(user);
        assertThat(like.getReview()).isSameAs(review);
        assertThat(menuLike.getMenu()).isSameAs(menu);
        assertThat(menuLike.getIsLike()).isTrue();
        menuLike.updateLike(false);
        assertThat(menuLike.getIsLike()).isFalse();
    }

    @Test
    void createsReviewTranslationWithLanguageAndContent() {
        Review review = Review.builder().build();
        ReviewTranslation translation = ReviewTranslation.builder()
                .review(review).language(ssu.eatssu.domain.user.entity.Language.EN)
                .translatedContent("Great").charCount(5).build();

        assertThat(translation.getReview()).isSameAs(review);
        assertThat(translation.getLanguage()).isEqualTo(ssu.eatssu.domain.user.entity.Language.EN);
        assertThat(translation.getTranslatedContent()).isEqualTo("Great");
        assertThat(translation.getCharCount()).isEqualTo(5);
    }
}
