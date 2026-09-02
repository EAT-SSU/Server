package ssu.eatssu.domain.admin.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.admin.dto.BriefMenu;
import ssu.eatssu.domain.admin.dto.MealInfo;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.menu.persistence.MealMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LoadMealRepositoryTest {

    private static final Date DATE = new Date();

    @Autowired
    private LoadMealRepository loadMealRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MealMenuRepository mealMenuRepository;

    @BeforeEach
    void setup() {
        mealMenuRepository.deleteAll();
        mealRepository.deleteAll();
        menuRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        mealMenuRepository.deleteAll();
        mealRepository.deleteAll();
        menuRepository.deleteAll();
    }

    @Test
    void existsMealReturnsMealIdsMatchingRestaurantDateAndTimePart() {
        Meal meal = mealRepository.save(new Meal(DATE, TimePart.LUNCH, Restaurant.DODAM));
        mealRepository.save(new Meal(DATE, TimePart.DINNER, Restaurant.DODAM));

        List<Long> mealIds = loadMealRepository.existsMeal(new MealInfo(Restaurant.DODAM, DATE, TimePart.LUNCH));

        assertThat(mealIds).containsExactly(meal.getId());
    }

    @Test
    void findMenuNamesByMealIdReturnsNamesSortedAscending() {
        Meal meal = mealRepository.save(new Meal(DATE, TimePart.LUNCH, Restaurant.DODAM));
        Menu menuB = menuRepository.save(Menu.createVariable("제육볶음", Restaurant.DODAM));
        Menu menuA = menuRepository.save(Menu.createVariable("김치찌개", Restaurant.DODAM));
        mealMenuRepository.save(MealMenu.builder().meal(meal).menu(menuB).build());
        mealMenuRepository.save(MealMenu.builder().meal(meal).menu(menuA).build());

        List<String> menuNames = loadMealRepository.findMenuNamesByMealId(meal.getId());

        assertThat(menuNames).containsExactly("김치찌개", "제육볶음");
    }

    @Test
    void getAllMealIdsByInfoReturnsMealIdsMatchingInfo() {
        Meal meal = mealRepository.save(new Meal(DATE, TimePart.LUNCH, Restaurant.DODAM));

        List<Long> mealIds = loadMealRepository.getAllMealIdsByInfo(new MealInfo(Restaurant.DODAM, DATE, TimePart.LUNCH));

        assertThat(mealIds).containsExactly(meal.getId());
    }

    @Test
    void findBriefMenusByMealIdReturnsMenusInMeal() {
        Meal meal = mealRepository.save(new Meal(DATE, TimePart.LUNCH, Restaurant.DODAM));
        Menu menu = menuRepository.save(Menu.createVariable("제육볶음", Restaurant.DODAM));
        mealMenuRepository.save(MealMenu.builder().meal(meal).menu(menu).build());

        List<BriefMenu> briefMenus = loadMealRepository.findBriefMenusByMealId(meal.getId());

        assertThat(briefMenus).extracting(BriefMenu::name).containsExactly("제육볶음");
    }

    @Test
    void findMenuReturnsMenuByNameAndRestaurant() {
        menuRepository.save(Menu.createVariable("제육볶음", Restaurant.DODAM));

        Menu found = loadMealRepository.findMenu("제육볶음", Restaurant.DODAM);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("제육볶음");
    }

    @Test
    void findMenuReturnsNullWhenMenuDoesNotExist() {
        Menu found = loadMealRepository.findMenu("제육볶음", Restaurant.DODAM);

        assertThat(found).isNull();
    }

    @Test
    void getAllMenuIdsReturnsMenuIdsInMeal() {
        Meal meal = mealRepository.save(new Meal(DATE, TimePart.LUNCH, Restaurant.DODAM));
        Menu menu = menuRepository.save(Menu.createVariable("제육볶음", Restaurant.DODAM));
        mealMenuRepository.save(MealMenu.builder().meal(meal).menu(menu).build());

        List<Long> menuIds = loadMealRepository.getAllMenuIds(meal.getId());

        assertThat(menuIds).containsExactly(menu.getId());
    }

    @Test
    void countMealMenuByMenuIdReturnsUsageCount() {
        Meal meal = mealRepository.save(new Meal(DATE, TimePart.LUNCH, Restaurant.DODAM));
        Menu menu = menuRepository.save(Menu.createVariable("제육볶음", Restaurant.DODAM));
        mealMenuRepository.save(MealMenu.builder().meal(meal).menu(menu).build());

        int count = loadMealRepository.countMealMenuByMenuId(menu.getId());

        assertThat(count).isEqualTo(1);
    }
}
