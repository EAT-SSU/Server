package ssu.eatssu.domain.review.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewImageTest {

    @Test
    void linksReviewAndStoresImageUrl() {
        Review review = Review.builder().id(1L).build();

        ReviewImage image = new ReviewImage(review, "https://cdn.example/review.jpg");

        assertThat(image.getReview()).isSameAs(review);
        assertThat(image.getImageUrl()).isEqualTo("https://cdn.example/review.jpg");
        assertThat(image.getId()).isNull();
    }

    @Test
    void buildsViaBuilder() {
        Review review = Review.builder().id(1L).build();

        ReviewImage image = ReviewImage.builder()
                                        .id(10L)
                                        .review(review)
                                        .imageUrl("https://cdn.example/built.jpg")
                                        .build();

        assertThat(image.getId()).isEqualTo(10L);
        assertThat(image.getReview()).isSameAs(review);
        assertThat(image.getImageUrl()).isEqualTo("https://cdn.example/built.jpg");
    }
}
