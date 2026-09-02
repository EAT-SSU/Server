package ssu.eatssu.domain.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.admin.dto.MealInfo;
import ssu.eatssu.domain.admin.dto.request.RegisterMealRequest;
import ssu.eatssu.domain.admin.dto.response.MenuBoards;
import ssu.eatssu.domain.admin.service.ManageMealService;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.slack.service.SlackErrorNotifier;
import ssu.eatssu.global.handler.GlobalExceptionHandler;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ManageMealControllerTest {

    @Mock
    private ManageMealService manageMealService;

    @Mock
    private SlackErrorNotifier slackErrorNotifier;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ManageMealController(manageMealService))
                                 .setControllerAdvice(new GlobalExceptionHandler(slackErrorNotifier))
                                 .build();
    }

    private Date date(String yyyyMMdd) throws Exception {
        return new SimpleDateFormat("yyyyMMdd").parse(yyyyMMdd);
    }

    @Test
    void returnsMealBoards() throws Exception {
        when(manageMealService.getMenuBoards(any(), eq(TimePart.LUNCH))).thenReturn(new MenuBoards());

        mockMvc.perform(get("/admin/meals")
                                .param("date", "20260101")
                                .param("timePart", "LUNCH"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        verify(manageMealService).getMenuBoards(eq(date("20260101")), eq(TimePart.LUNCH));
    }

    @Test
    void registersMeal() throws Exception {
        mockMvc.perform(post("/admin/meals")
                                .param("restaurant", Restaurant.DODAM.name())
                                .param("date", "20260101")
                                .param("timePart", "LUNCH")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"menuNames\":[\"제육볶음\"]}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        MealInfo mealInfo = new MealInfo(Restaurant.DODAM, date("20260101"), TimePart.LUNCH);
        verify(manageMealService).register(eq(mealInfo),
                                            eq(new RegisterMealRequest(new ArrayList<>(List.of("제육볶음")))));
    }

    @Test
    void returnsConflictWhenMealAlreadyRegistered() throws Exception {
        doThrow(new BaseException(BaseResponseStatus.CONFLICT))
                .when(manageMealService).register(any(), any());

        mockMvc.perform(post("/admin/meals")
                                .param("restaurant", Restaurant.DODAM.name())
                                .param("date", "20260101")
                                .param("timePart", "LUNCH")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"menuNames\":[\"제육볶음\"]}"))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void deletesMealById() throws Exception {
        mockMvc.perform(delete("/admin/meals/{mealId}", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        verify(manageMealService).delete(eq(1L));
    }
}
