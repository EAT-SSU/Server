package ssu.eatssu.domain.admin.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.menu.persistence.MenuCategoryRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LoadCategoryRepositoryTest {

    @Autowired
    private LoadCategoryRepository loadCategoryRepository;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @BeforeEach
    void setup() {
        menuCategoryRepository.deleteAll();
    }

    @Test
    void existsCategoryReturnsTrueWhenCategoryExistsForRestaurant() {
        menuCategoryRepository.save(MenuCategory.builder().name("한식").restaurant(Restaurant.FOOD_COURT).build());

        boolean exists = loadCategoryRepository.existsCategory(Restaurant.FOOD_COURT, "한식");

        assertThat(exists).isTrue();
    }

    @Test
    void existsCategoryReturnsFalseWhenCategoryDoesNotExist() {
        boolean exists = loadCategoryRepository.existsCategory(Restaurant.FOOD_COURT, "한식");

        assertThat(exists).isFalse();
    }
}
