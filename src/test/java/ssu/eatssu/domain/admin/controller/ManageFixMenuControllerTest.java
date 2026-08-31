package ssu.eatssu.domain.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.admin.dto.request.RegisterFixMenuRequest;
import ssu.eatssu.domain.admin.dto.request.UpdateFixMenuRequest;
import ssu.eatssu.domain.admin.dto.response.MenuBoards;
import ssu.eatssu.domain.admin.service.ManageFixMenuService;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.slack.service.SlackErrorNotifier;
import ssu.eatssu.global.handler.GlobalExceptionHandler;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ManageFixMenuControllerTest {

    @Mock
    private ManageFixMenuService manageFixMenuService;

    @Mock
    private SlackErrorNotifier slackErrorNotifier;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ManageFixMenuController(manageFixMenuService))
                                 .setControllerAdvice(new GlobalExceptionHandler(slackErrorNotifier))
                                 .build();
    }

    @Test
    void returnsFixMenuBoards() throws Exception {
        when(manageFixMenuService.getMenuBoards()).thenReturn(new MenuBoards());

        mockMvc.perform(get("/admin/menu/fix-menus"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    void registersFixMenu() throws Exception {
        mockMvc.perform(post("/admin/menu/fix-menus")
                                .param("restaurant", Restaurant.HAKSIK.name())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"categoryId\":1,\"name\":\"제육덮밥\",\"price\":5000}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        verify(manageFixMenuService).register(eq(Restaurant.HAKSIK),
                                               eq(new RegisterFixMenuRequest(1L, "제육덮밥", 5000)));
    }

    @Test
    void returnsConflictWhenFixMenuAlreadyExists() throws Exception {
        doThrow(new BaseException(BaseResponseStatus.CONFLICT))
                .when(manageFixMenuService).register(eq(Restaurant.HAKSIK),
                                                       eq(new RegisterFixMenuRequest(1L, "제육덮밥", 5000)));

        mockMvc.perform(post("/admin/menu/fix-menus")
                                .param("restaurant", Restaurant.HAKSIK.name())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"categoryId\":1,\"name\":\"제육덮밥\",\"price\":5000}"))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void updatesFixMenu() throws Exception {
        mockMvc.perform(patch("/admin/menu/fix-menus/{menuId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"제육덮밥\",\"price\":5500}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        verify(manageFixMenuService).updateMenu(eq(1L), eq(new UpdateFixMenuRequest("제육덮밥", 5500)));
    }

    @Test
    void returnsNotFoundWhenUpdatingMissingMenu() throws Exception {
        doThrow(new BaseException(BaseResponseStatus.NOT_FOUND_MENU))
                .when(manageFixMenuService).updateMenu(eq(1L), eq(new UpdateFixMenuRequest("제육덮밥", 5500)));

        mockMvc.perform(patch("/admin/menu/fix-menus/{menuId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"제육덮밥\",\"price\":5500}"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void deletesFixMenuById() throws Exception {
        mockMvc.perform(delete("/admin/menu/fix-menus/{menuId}", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        verify(manageFixMenuService).delete(eq(1L));
    }

    @Test
    void togglesDiscontinuedStatus() throws Exception {
        when(manageFixMenuService.changeDiscontinuedStatus(eq(1L))).thenReturn(true);

        mockMvc.perform(patch("/admin/menu/fix-menus/{menuId}/discontinued-status", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.result").value(true));
    }

    @Test
    void returnsBadRequestWhenTogglingFixedMenu() throws Exception {
        doThrow(new BaseException(BaseResponseStatus.NOT_SUPPORT_RESTAURANT))
                .when(manageFixMenuService).changeDiscontinuedStatus(eq(1L));

        mockMvc.perform(patch("/admin/menu/fix-menus/{menuId}/discontinued-status", 1L))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.isSuccess").value(false));
    }
}
