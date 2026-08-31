package ssu.eatssu.domain.review.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewQuerydslTest {

    @Test
    void exposesQuerydslPathsForReviewEntities() {
        QReview review = QReview.review;
        QReviewImage image = QReviewImage.reviewImage;
        QReviewLike like = QReviewLike.reviewLike;
        QReviewMenuLike menuLike = QReviewMenuLike.reviewMenuLike;
        QReviewTranslation translation = QReviewTranslation.reviewTranslation;
        QReport report = QReport.report;
        QReviews reviews = QReviews.reviews1;

        assertThat(review.getType()).isEqualTo(Review.class);
        assertThat(review.content.getMetadata().getName()).isEqualTo("content");
        assertThat(review.rating.getMetadata().getName()).isEqualTo("rating");
        assertThat(review.menuLikes.getMetadata().getName()).isEqualTo("menuLikes");
        assertThat(review.reviewImages.getMetadata().getName()).isEqualTo("reviewImages");
        assertThat(image.imageUrl.getMetadata().getName()).isEqualTo("imageUrl");
        assertThat(like.user.getMetadata().getName()).isEqualTo("user");
        assertThat(menuLike.isLike.getMetadata().getName()).isEqualTo("isLike");
        assertThat(translation.translatedContent.getMetadata().getName()).isEqualTo("translatedContent");
        assertThat(translation.language.getMetadata().getName()).isEqualTo("language");
        assertThat(report.status.getMetadata().getName()).isEqualTo("status");
        assertThat(reviews.reviews.getMetadata().getName()).isEqualTo("reviews");
    }
}
