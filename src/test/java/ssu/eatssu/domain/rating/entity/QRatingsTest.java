package ssu.eatssu.domain.rating.entity;

import com.querydsl.core.types.PathMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QRatingsTest {

    @Test
    void exposesQuerydslPathsForRatingsFields() {
        QRatings query = QRatings.ratings;

        assertThat(query.getType()).isEqualTo(Ratings.class);
        assertThat(query.mainRating.getMetadata().getName()).isEqualTo("mainRating");
        assertThat(query.amountRating.getMetadata().getName()).isEqualTo("amountRating");
        assertThat(query.tasteRating.getMetadata().getName()).isEqualTo("tasteRating");
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        PathMetadata metadata = QRatings.ratings.getMetadata();

        assertThat(new QRatings("ratings").getType()).isEqualTo(Ratings.class);
        assertThat(new QRatings(QRatings.ratings).getType()).isEqualTo(Ratings.class);
        assertThat(new QRatings(metadata).getType()).isEqualTo(Ratings.class);
    }
}
