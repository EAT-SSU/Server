package ssu.eatssu.domain.menu.service;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.menu.dto.MenuResponse.MenuInformationResponse;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.repository.MenuRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

@SpringBootTest
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Test
    void 식당_이름으로_고정_메뉴를_조회한다() {
        // given
        List<Menu> menus = new ArrayList<>();
        Restaurant foodCourt = Restaurant.from("푸드 코트");
        menus.add(Menu.createFixed("라면", foodCourt, 3000));
        menus.add(Menu.createFixed("떡볶이", foodCourt, 5000));
        menus.add(Menu.createFixed("짜게치", foodCourt, 4000));
        menuRepository.saveAll(menus);

        // when & then
        Assertions.assertThat(menuService.findMenusByRestaurant(foodCourt))
            .extracting(MenuInformationResponse::getName)
            .containsExactly("라면", "떡볶이", "짜게치");
    }
}