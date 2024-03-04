package ssu.eatssu.domain.menu.service;

import static org.assertj.core.api.Assertions.*;
import static ssu.eatssu.domain.menu.entity.TimePart.LUNCH;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.menu.dto.MenuRequest.MealCreateRequest;
import ssu.eatssu.domain.menu.dto.MenuResponse.MenuInformationResponse;
import ssu.eatssu.domain.menu.dto.MenuResponse.MenusInformationResponse;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.menu.repository.MealRepository;
import ssu.eatssu.domain.menu.repository.MenuCategoryRepository;
import ssu.eatssu.domain.menu.repository.MenuRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

@SpringBootTest
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @BeforeEach
    void setUp() {
        menuRepository.deleteAll();
        mealRepository.deleteAll();
    }

    @Test
    void 식당_이름으로_고정_메뉴를_조회한다() {
        // given
        List<Menu> menus = new ArrayList<>();
        Restaurant foodCourt = Restaurant.from("FOOD_COURT");
        MenuCategory category1 = MenuCategory.builder().name("분식").restaurant(foodCourt).build();
        MenuCategory category2 = MenuCategory.builder().name("한식").restaurant(foodCourt).build();
        menus.add(Menu.createFixed("라면", foodCourt, 3000, category1));
        menus.add(Menu.createFixed("떡볶이", foodCourt, 5000, category2));
        menus.add(Menu.createFixed("짜게치", foodCourt, 4000, category1));
        menuCategoryRepository.save(category1);
        menuCategoryRepository.save(category2);
        menuRepository.saveAll(menus);

        // when
        Map<String, List<MenuInformationResponse>> response = menuService.findMenusByRestaurant(
            foodCourt);

        // then
        assertThat(response).hasSize(2);
        for (String key : response.keySet()) {
            System.out.print(key + " : ");
            response.get(key).forEach(menu -> System.out.print(menu.getName() + " "));
            System.out.print("\n");
        }
    }

    @Test
    void 식단을_생성한다() {
        식단_생성_요청();

        // then
        assertThat(mealRepository.findAll()).hasSize(1);
        assertThat(menuRepository.findAll()).hasSize(3);
        assertThat(menuRepository.findAll())
            .extracting(Menu::getName)
            .containsExactly("돈까스", "샐러드", "김치");
    }

    private Long 식단_생성_요청() {
        // given
        Date date = Date.valueOf("2024-01-03");
        Restaurant haksik = Restaurant.from("HAKSIK");
        MealCreateRequest request = new MealCreateRequest(List.of("돈까스", "샐러드", "김치"));

        // when
        menuService.createMeal(date, haksik, LUNCH, request);

        return mealRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Meal not created"))
            .getId();
    }

    @Test
    void 식단을_조회한다() {
        // given
        Long createdMealId = 식단_생성_요청();

        // when
        MenusInformationResponse response = menuService.findMenusInMeal(createdMealId);

        // then
        assertThat(response.getMenusInformation()).hasSize(3);
    }

    @Test
    void 식단을_삭제한다() {
        // given
        Long createdMealId = 식단_생성_요청();

        // when
        menuService.deleteMeal(createdMealId);

        // then
        assertThat(mealRepository.findAll()).hasSize(0);
        assertThat(menuRepository.findAll()).hasSize(0);
    }
}