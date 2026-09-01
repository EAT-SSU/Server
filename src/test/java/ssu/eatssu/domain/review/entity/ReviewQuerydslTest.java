package ssu.eatssu.domain.review.entity;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.PathInits;
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
        assertThat(review.id.getMetadata().getName()).isEqualTo("id");
        assertThat(review.content.getMetadata().getName()).isEqualTo("content");
        assertThat(review.rating.getMetadata().getName()).isEqualTo("rating");
        assertThat(review.createdDate.getMetadata().getName()).isEqualTo("createdDate");
        assertThat(review.modifiedDate.getMetadata().getName()).isEqualTo("modifiedDate");
        assertThat(review.menuLikes.getMetadata().getName()).isEqualTo("menuLikes");
        assertThat(review.reviewImages.getMetadata().getName()).isEqualTo("reviewImages");
        assertThat(review.reviewLikes.getMetadata().getName()).isEqualTo("reviewLikes");
        assertThat(review.meal).isNotNull();
        assertThat(review.menu).isNotNull();
        assertThat(review.ratings).isNotNull();
        assertThat(review.user).isNotNull();

        assertThat(image.getType()).isEqualTo(ReviewImage.class);
        assertThat(image.id.getMetadata().getName()).isEqualTo("id");
        assertThat(image.imageUrl.getMetadata().getName()).isEqualTo("imageUrl");
        assertThat(image.review).isNotNull();

        assertThat(like.getType()).isEqualTo(ReviewLike.class);
        assertThat(like.id.getMetadata().getName()).isEqualTo("id");
        assertThat(like.user.getMetadata().getName()).isEqualTo("user");
        assertThat(like.review).isNotNull();

        assertThat(menuLike.getType()).isEqualTo(ReviewMenuLike.class);
        assertThat(menuLike.id.getMetadata().getName()).isEqualTo("id");
        assertThat(menuLike.isLike.getMetadata().getName()).isEqualTo("isLike");
        assertThat(menuLike.menu).isNotNull();
        assertThat(menuLike.review).isNotNull();

        assertThat(translation.getType()).isEqualTo(ReviewTranslation.class);
        assertThat(translation.id.getMetadata().getName()).isEqualTo("id");
        assertThat(translation.translatedContent.getMetadata().getName()).isEqualTo("translatedContent");
        assertThat(translation.language.getMetadata().getName()).isEqualTo("language");
        assertThat(translation.charCount.getMetadata().getName()).isEqualTo("charCount");
        assertThat(translation.createdDate.getMetadata().getName()).isEqualTo("createdDate");
        assertThat(translation.modifiedDate.getMetadata().getName()).isEqualTo("modifiedDate");
        assertThat(translation.review).isNotNull();

        assertThat(report.getType()).isEqualTo(Report.class);
        assertThat(report.id.getMetadata().getName()).isEqualTo("id");
        assertThat(report.content.getMetadata().getName()).isEqualTo("content");
        assertThat(report.reportType.getMetadata().getName()).isEqualTo("reportType");
        assertThat(report.status.getMetadata().getName()).isEqualTo("status");
        assertThat(report.createdDate.getMetadata().getName()).isEqualTo("createdDate");
        assertThat(report.modifiedDate.getMetadata().getName()).isEqualTo("modifiedDate");
        assertThat(report.review).isNotNull();
        assertThat(report.user).isNotNull();

        assertThat(reviews.reviews.getMetadata().getName()).isEqualTo("reviews");
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        PathMetadata reviewMetadata = QReview.review.getMetadata();
        assertThat(new QReview("review").getType()).isEqualTo(Review.class);
        assertThat(new QReview(QReview.review).getType()).isEqualTo(Review.class);
        assertThat(new QReview(reviewMetadata).getType()).isEqualTo(Review.class);
        assertThat(new QReview(reviewMetadata, PathInits.DIRECT2).getType()).isEqualTo(Review.class);
        assertThat(new QReview(Review.class, reviewMetadata, PathInits.DIRECT2).getType()).isEqualTo(Review.class);

        PathMetadata imageMetadata = QReviewImage.reviewImage.getMetadata();
        assertThat(new QReviewImage("reviewImage").getType()).isEqualTo(ReviewImage.class);
        assertThat(new QReviewImage(QReviewImage.reviewImage).getType()).isEqualTo(ReviewImage.class);
        assertThat(new QReviewImage(imageMetadata).getType()).isEqualTo(ReviewImage.class);
        assertThat(new QReviewImage(imageMetadata, PathInits.DIRECT2).getType()).isEqualTo(ReviewImage.class);
        assertThat(new QReviewImage(ReviewImage.class, imageMetadata, PathInits.DIRECT2).getType())
                .isEqualTo(ReviewImage.class);

        PathMetadata likeMetadata = QReviewLike.reviewLike.getMetadata();
        assertThat(new QReviewLike("reviewLike").getType()).isEqualTo(ReviewLike.class);
        assertThat(new QReviewLike(QReviewLike.reviewLike).getType()).isEqualTo(ReviewLike.class);
        assertThat(new QReviewLike(likeMetadata).getType()).isEqualTo(ReviewLike.class);
        assertThat(new QReviewLike(likeMetadata, PathInits.DIRECT2).getType()).isEqualTo(ReviewLike.class);
        assertThat(new QReviewLike(ReviewLike.class, likeMetadata, PathInits.DIRECT2).getType())
                .isEqualTo(ReviewLike.class);

        PathMetadata menuLikeMetadata = QReviewMenuLike.reviewMenuLike.getMetadata();
        assertThat(new QReviewMenuLike("reviewMenuLike").getType()).isEqualTo(ReviewMenuLike.class);
        assertThat(new QReviewMenuLike(QReviewMenuLike.reviewMenuLike).getType()).isEqualTo(ReviewMenuLike.class);
        assertThat(new QReviewMenuLike(menuLikeMetadata).getType()).isEqualTo(ReviewMenuLike.class);
        assertThat(new QReviewMenuLike(menuLikeMetadata, PathInits.DIRECT2).getType()).isEqualTo(ReviewMenuLike.class);
        assertThat(new QReviewMenuLike(ReviewMenuLike.class, menuLikeMetadata, PathInits.DIRECT2).getType())
                .isEqualTo(ReviewMenuLike.class);

        PathMetadata translationMetadata = QReviewTranslation.reviewTranslation.getMetadata();
        assertThat(new QReviewTranslation("reviewTranslation").getType()).isEqualTo(ReviewTranslation.class);
        assertThat(new QReviewTranslation(QReviewTranslation.reviewTranslation).getType())
                .isEqualTo(ReviewTranslation.class);
        assertThat(new QReviewTranslation(translationMetadata).getType()).isEqualTo(ReviewTranslation.class);
        assertThat(new QReviewTranslation(translationMetadata, PathInits.DIRECT2).getType())
                .isEqualTo(ReviewTranslation.class);
        assertThat(new QReviewTranslation(ReviewTranslation.class, translationMetadata, PathInits.DIRECT2).getType())
                .isEqualTo(ReviewTranslation.class);

        PathMetadata reportMetadata = QReport.report.getMetadata();
        assertThat(new QReport("report").getType()).isEqualTo(Report.class);
        assertThat(new QReport(QReport.report).getType()).isEqualTo(Report.class);
        assertThat(new QReport(reportMetadata).getType()).isEqualTo(Report.class);
        assertThat(new QReport(reportMetadata, PathInits.DIRECT2).getType()).isEqualTo(Report.class);
        assertThat(new QReport(Report.class, reportMetadata, PathInits.DIRECT2).getType()).isEqualTo(Report.class);

        PathMetadata reviewsMetadata = QReviews.reviews1.getMetadata();
        assertThat(new QReviews("reviews1").getType()).isEqualTo(Reviews.class);
        assertThat(new QReviews(QReviews.reviews1).getType()).isEqualTo(Reviews.class);
        assertThat(new QReviews(reviewsMetadata).getType()).isEqualTo(Reviews.class);
    }
}
