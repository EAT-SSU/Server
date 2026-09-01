package ssu.eatssu.domain.menu.entity.constants;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuTypeTest {

    @Test
    void fromReturnsMatchingMenuTypeByDescription() {
        assertThat(MenuType.from("고정 메뉴")).isEqualTo(MenuType.FIXED);
        assertThat(MenuType.from("변동 메뉴")).isEqualTo(MenuType.VARIABLE);
    }

    @Test
    void fromThrowsWhenDescriptionIsUnknown() {
        assertThatThrownBy(() -> MenuType.from("존재하지않음"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void eachMenuTypeExposesItsRestaurants() {
        assertThat(MenuType.FIXED.getRestaurants()).containsExactly(Restaurant.FOOD_COURT, Restaurant.SNACK_CORNER);
        assertThat(MenuType.VARIABLE.getRestaurants())
                .containsExactly(Restaurant.DODAM, Restaurant.DORMITORY, Restaurant.HAKSIK);
    }
}
