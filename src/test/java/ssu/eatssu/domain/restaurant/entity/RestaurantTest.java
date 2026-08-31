package ssu.eatssu.domain.restaurant.entity;

import org.junit.jupiter.api.Test;
import ssu.eatssu.global.handler.response.BaseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestaurantTest {

    @Test
    void 식당_이름을_enum으로_변환한다() {
        assertThat(Restaurant.from("FOOD_COURT")).isEqualTo(Restaurant.FOOD_COURT);
        assertThatThrownBy(() -> Restaurant.from("UNKNOWN")).isInstanceOf(BaseException.class);
    }
}
