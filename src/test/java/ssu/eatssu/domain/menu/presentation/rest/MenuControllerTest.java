package ssu.eatssu.domain.menu.presentation.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.menu.presentation.dto.response.MenuRestaurantResponse;
import ssu.eatssu.domain.menu.service.MenuService;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    @Mock
    private MenuService menuService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MenuController(menuService)).build();
    }

    @Test
    void 식당별_메뉴를_조회한다() throws Exception {
        when(menuService.getMenusByRestaurant(Restaurant.FOOD_COURT, null)).thenReturn(MenuRestaurantResponse.init());

        mockMvc.perform(get("/menus").param("restaurant", Restaurant.FOOD_COURT.name()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.result.categoryMenuListCollection").isArray());

        verify(menuService).getMenusByRestaurant(Restaurant.FOOD_COURT, null);
    }
}
