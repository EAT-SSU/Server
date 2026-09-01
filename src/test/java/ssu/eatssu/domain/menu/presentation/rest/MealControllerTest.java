package ssu.eatssu.domain.menu.presentation.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.menu.presentation.dto.response.MealCreateResult;
import ssu.eatssu.domain.menu.presentation.dto.response.MenusInMealResponse;
import ssu.eatssu.domain.menu.service.MealService;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.slack.service.SlackErrorNotifier;
import ssu.eatssu.global.handler.GlobalExceptionHandler;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MealControllerTest {

    @Mock
    private MealService mealService;

    @Mock
    private SlackErrorNotifier slackErrorNotifier;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MealController(mealService))
                                 .setControllerAdvice(new GlobalExceptionHandler(slackErrorNotifier))
                                 .build();
    }

    @Test
    void createsMealWhenRestaurantIsVariableType() throws Exception {
        when(mealService.createMeal(any(), eq(Restaurant.DODAM), eq(TimePart.LUNCH), any()))
                .thenReturn(new MealCreateResult(1L, List.of()));

        mockMvc.perform(post("/meals")
                                .param("date", "20260101")
                                .param("restaurant", Restaurant.DODAM.name())
                                .param("time", TimePart.LUNCH.name())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"menuNames\":[\"돈까스\"]}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.result.mealId").value(1));
    }

    @Test
    void returnsBadRequestWhenCreatingMealForFixedRestaurant() throws Exception {
        mockMvc.perform(post("/meals")
                                .param("date", "20260101")
                                .param("restaurant", Restaurant.FOOD_COURT.name())
                                .param("time", TimePart.LUNCH.name())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"menuNames\":[\"돈까스\"]}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.isSuccess").value(false));

        verifyNoInteractions(mealService);
    }

    @Test
    void createsMealWithPriceWhenRestaurantIsVariableType() throws Exception {
        when(mealService.createMealWithPrice(any(), eq(Restaurant.DODAM), eq(TimePart.LUNCH), any()))
                .thenReturn(new MealCreateResult(1L, List.of()));

        mockMvc.perform(post("/meals/with-price")
                                .param("date", "20260101")
                                .param("restaurant", Restaurant.DODAM.name())
                                .param("time", TimePart.LUNCH.name())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"menuNames\":[\"돈까스\"],\"price\":3000}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.result.mealId").value(1));
    }

    @Test
    void returnsBadRequestWhenCreatingMealWithPriceForFixedRestaurant() throws Exception {
        mockMvc.perform(post("/meals/with-price")
                                .param("date", "20260101")
                                .param("restaurant", Restaurant.FOOD_COURT.name())
                                .param("time", TimePart.LUNCH.name())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"menuNames\":[\"돈까스\"],\"price\":3000}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.isSuccess").value(false));

        verifyNoInteractions(mealService);
    }

    @Test
    void returnsMealDetails() throws Exception {
        when(mealService.getMealDetailsByDateAndRestaurantAndTimePart(any(), eq(Restaurant.DODAM),
                                                                       eq(TimePart.LUNCH), isNull()))
                .thenReturn(List.of());

        mockMvc.perform(get("/meals")
                                .param("date", "20260101")
                                .param("restaurant", Restaurant.DODAM.name())
                                .param("time", TimePart.LUNCH.name()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.result").isArray());
    }

    @Test
    void deletesMealById() throws Exception {
        mockMvc.perform(delete("/meals/{mealId}", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        verify(mealService).deleteByMealId(eq(1L));
    }

    @Test
    void returnsMenusInMeal() throws Exception {
        when(mealService.getMenusInMealByMealId(eq(1L), isNull()))
                .thenReturn(new MenusInMealResponse(List.of()));

        mockMvc.perform(get("/meals/{mealId}/menus-info", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.result.briefMenus").isArray());
    }
}
