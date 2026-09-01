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

    @Test
    void restaurantTypeExposesDescriptionAndRestaurants() {
        assertThat(RestaurantType.FIXED.getDescription()).isEqualTo("고정메뉴 식당");
        assertThat(RestaurantType.VARIABLE.getDescription()).isEqualTo("변동메뉴 식당");
        assertThat(RestaurantType.FIXED.getRestaurants()).containsExactly(Restaurant.FOOD_COURT, Restaurant.SNACK_CORNER);
    }

    @Test
    void restaurantExposesNameAndPriceForEveryConstant() {
        assertThat(Restaurant.DORMITORY.getRestaurantName()).isEqualTo("DORMITORY");
        assertThat(Restaurant.DORMITORY.getRestaurantPrice()).isEqualTo(5500);
        assertThat(Restaurant.HAKSIK.getRestaurantPrice()).isEqualTo(5000);
        assertThat(Restaurant.FOOD_COURT.getRestaurantPrice()).isNull();
        assertThat(Restaurant.SNACK_CORNER.getRestaurantPrice()).isNull();
        assertThat(Restaurant.FACULTY.getRestaurantPrice()).isNull();
    }
}
