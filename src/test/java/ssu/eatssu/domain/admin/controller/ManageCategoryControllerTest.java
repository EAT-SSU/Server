package ssu.eatssu.domain.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.admin.dto.request.RegisterCategoryRequest;
import ssu.eatssu.domain.admin.service.ManageCategoryService;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.slack.service.SlackErrorNotifier;
import ssu.eatssu.global.handler.GlobalExceptionHandler;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ManageCategoryControllerTest {

    @Mock
    private ManageCategoryService manageCategoryService;

    @Mock
    private SlackErrorNotifier slackErrorNotifier;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ManageCategoryController(manageCategoryService))
                                 .setControllerAdvice(new GlobalExceptionHandler(slackErrorNotifier))
                                 .build();
    }

    @Test
    void 카테고리를_등록한다() throws Exception {
        mockMvc.perform(post("/admin/menu/category/")
                                .param("restaurant", Restaurant.FOOD_COURT.name())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"한식\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.code").value(1000));

        verify(manageCategoryService).register(eq(Restaurant.FOOD_COURT), eq(new RegisterCategoryRequest("한식")));
    }

    @Test
    void 중복된_카테고리면_409를_반환한다() throws Exception {
        doThrow(new BaseException(BaseResponseStatus.CONFLICT))
                .when(manageCategoryService).register(eq(Restaurant.FOOD_COURT), eq(new RegisterCategoryRequest("한식")));

        mockMvc.perform(post("/admin/menu/category/")
                                .param("restaurant", Restaurant.FOOD_COURT.name())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"한식\"}"))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.isSuccess").value(false))
               .andExpect(jsonPath("$.code").value(409));
    }
}
