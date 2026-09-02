package ssu.eatssu.domain.menu.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MealMenuQueryRepositoryTest {

    @Autowired
    private MealMenuQueryRepository mealMenuQueryRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MealMenuRepository mealMenuRepository;

    @BeforeEach
    void setup() {
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        mealMenuRepository.deleteAll();
        mealRepository.deleteAll();
        menuRepository.deleteAll();
    }

    @Test
    void getMenuIdsReturnsMenuIdsLinkedToMeal() {
        // given
        Meal meal = mealRepository.save(new Meal(new Date(), TimePart.LUNCH, Restaurant.DODAM));
        Menu menu = menuRepository.save(Menu.createVariable("제육볶음", Restaurant.DODAM));
        mealMenuRepository.save(MealMenu.builder().meal(meal).menu(menu).build());

        // when
        List<Long> menuIds = mealMenuQueryRepository.getMenuIds(meal.getId());

        // then
        assertThat(menuIds).containsExactly(menu.getId());
    }

    @Test
    void getMenuIdsReturnsEmptyListWhenMealHasNoMenus() {
        // given
        Meal meal = mealRepository.save(new Meal(new Date(), TimePart.LUNCH, Restaurant.DODAM));

        // when
        List<Long> menuIds = mealMenuQueryRepository.getMenuIds(meal.getId());

        // then
        assertThat(menuIds).isEmpty();
    }
}
