package ssu.eatssu.domain.rating.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RatingsTest {

    @Test
    void 평점은_1에서_5_사이여야_한다() {
        assertThat(Ratings.of(5, null, 1).getMainRating()).isEqualTo(5);
        assertThatThrownBy(() -> Ratings.of(6, 3, 3)).isInstanceOf(IllegalArgumentException.class);
    }
}
