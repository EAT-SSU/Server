package ssu.eatssu.domain.admin.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.admin.dto.BriefMenu;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.menu.persistence.MenuCategoryRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LoadFixMenuRepositoryTest {

    @Autowired
    private LoadFixMenuRepository loadFixMenuRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @BeforeEach
    void setup() {
        menuRepository.deleteAll();
        menuCategoryRepository.deleteAll();
    }

    @Test
    void findBriefMenusByCategoryIdReturnsMenusInCategory() {
        MenuCategory category = menuCategoryRepository.save(
                MenuCategory.builder().name("한식").restaurant(Restaurant.FOOD_COURT).build());
        menuRepository.save(Menu.createFixed("라면", Restaurant.FOOD_COURT, 3000, category));

        List<BriefMenu> briefMenus = loadFixMenuRepository.findBriefMenusByCategoryId(category.getId());

        assertThat(briefMenus).extracting(BriefMenu::name).containsExactly("라면");
    }

    @Test
    void existsMenuReturnsTrueWhenMenuExistsForRestaurant() {
        menuRepository.save(Menu.createFixed("라면", Restaurant.FOOD_COURT, 3000, null));

        boolean exists = loadFixMenuRepository.existsMenu("라면", Restaurant.FOOD_COURT);

        assertThat(exists).isTrue();
    }

    @Test
    void existsMenuReturnsFalseWhenMenuDoesNotExist() {
        boolean exists = loadFixMenuRepository.existsMenu("라면", Restaurant.FOOD_COURT);

        assertThat(exists).isFalse();
    }

    @Test
    void getRestaurantReturnsRestaurantOfMenu() {
        Menu menu = menuRepository.save(Menu.createFixed("라면", Restaurant.FOOD_COURT, 3000, null));

        Restaurant restaurant = loadFixMenuRepository.getRestaurant(menu.getId());

        assertThat(restaurant).isEqualTo(Restaurant.FOOD_COURT);
    }

    @Test
    void findMenuCategoriesByRestaurantReturnsCategoriesForThatRestaurant() {
        menuCategoryRepository.save(MenuCategory.builder().name("한식").restaurant(Restaurant.FOOD_COURT).build());
        menuCategoryRepository.save(MenuCategory.builder().name("일식").restaurant(Restaurant.SNACK_CORNER).build());

        List<MenuCategory> categories = loadFixMenuRepository.findMenuCategoriesByRestaurant(Restaurant.FOOD_COURT);

        assertThat(categories).extracting(MenuCategory::getName).containsExactly("한식");
    }
}
