package ssu.eatssu.domain.menu.entity;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import static org.assertj.core.api.Assertions.assertThat;

class MenuCategoryTest {

    @Test
    void exposesIdNameAndRestaurant() {
        MenuCategory category = MenuCategory.builder().id(1L).name("한식").restaurant(Restaurant.FOOD_COURT).build();

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("한식");
        assertThat(category.getRestaurant()).isEqualTo(Restaurant.FOOD_COURT);
    }
}
