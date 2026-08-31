package ssu.eatssu.domain.restaurant.entity;

import org.junit.jupiter.api.Test;
import ssu.eatssu.global.handler.response.BaseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestaurantTest {

    @Test
    void fromReturnsMatchingRestaurant() {
        Restaurant restaurant = Restaurant.from("DODAM");

        assertThat(restaurant).isEqualTo(Restaurant.DODAM);
        assertThat(restaurant.getRestaurantPrice()).isEqualTo(6000);
    }

    @Test
    void fromThrowsForUnknownRestaurant() {
        assertThatThrownBy(() -> Restaurant.from("UNKNOWN"))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void restaurantTypeClassifiesFixedAndVariableRestaurants() {
        assertThat(RestaurantType.isFixedType(Restaurant.FOOD_COURT)).isTrue();
        assertThat(RestaurantType.isVariableType(Restaurant.DODAM)).isTrue();
        assertThat(RestaurantType.isFixedType(Restaurant.FACULTY)).isFalse();
        assertThat(RestaurantType.isVariableType(Restaurant.FACULTY)).isFalse();
    }
}
