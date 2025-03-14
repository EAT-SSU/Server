package ssu.eatssu.domain.menu.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ssu.eatssu.domain.menu.presentation.dto.response.MenuRestaurantResponse;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuCategoryRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("MenuService 테스트")
@ActiveProfiles("test")
class MenuServiceTest {

	@Autowired
	private MenuService menuService;

	@Autowired
	private MenuRepository menuRepository;

	@Autowired
	private MenuCategoryRepository menuCategoryRepository;

	@BeforeEach
	void setUp() {
		menuRepository.deleteAll();
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
		MenuRestaurantResponse response = menuService.getMenusByRestaurant(foodCourt);

		// then
		assertThat(response.getCategoryMenuListCollection()).hasSize(2);
	}
}