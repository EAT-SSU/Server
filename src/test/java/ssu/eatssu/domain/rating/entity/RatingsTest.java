package ssu.eatssu.domain.rating.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RatingsTest {

    @Test
    void 평점은_1에서_5_사이여야_한다() {
        Ratings ratings = Ratings.of(5, 4, 3);

        assertThat(ratings.getMainRating()).isEqualTo(5);
        assertThat(ratings.getAmountRating()).isEqualTo(4);
        assertThat(ratings.getTasteRating()).isEqualTo(3);
        assertThatThrownBy(() -> Ratings.of(6, 3, 3)).isInstanceOf(IllegalArgumentException.class);
    }
}
