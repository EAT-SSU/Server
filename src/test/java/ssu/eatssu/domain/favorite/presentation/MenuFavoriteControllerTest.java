package ssu.eatssu.domain.favorite.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.favorite.dto.response.FavoriteMenuResponse;
import ssu.eatssu.domain.favorite.dto.response.MenuSearchResponse;
import ssu.eatssu.domain.favorite.service.MenuFavoriteService;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.Role;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MenuFavoriteControllerTest {

    @Mock
    private MenuFavoriteService menuFavoriteService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MenuFavoriteController(menuFavoriteService))
                                 .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                                 .build();
    }

    @Test
    void searchMenusReturnsResultsWithFavoriteState() throws Exception {
        // given
        when(menuFavoriteService.searchMenus(any(), eq("파스")))
                .thenReturn(List.of(new MenuSearchResponse(1L, "파스타", Restaurant.DODAM, true)));

        // when & then
        mockMvc.perform(get("/menu-favorites/search").param("keyword", "파스")
                                                     .with(SecurityMockMvcRequestPostProcessors.user(userDetails())))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.result[0].menuName").value("파스타"))
               .andExpect(jsonPath("$.result[0].restaurant").value("DODAM"))
               .andExpect(jsonPath("$.result[0].isFavorite").value(true));
    }

    @Test
    void getFavoriteMenusReturnsFavorites() throws Exception {
        // given
        when(menuFavoriteService.getFavoriteMenus(any()))
                .thenReturn(List.of(new FavoriteMenuResponse(1L, "파스타", Restaurant.DODAM, false)));

        // when & then
        mockMvc.perform(get("/menu-favorites").with(SecurityMockMvcRequestPostProcessors.user(userDetails())))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result[0].menuId").value(1))
               .andExpect(jsonPath("$.result[0].isDiscontinued").value(false));
    }

    @Test
    void addFavoriteDelegatesToService() throws Exception {
        // when
        mockMvc.perform(post("/menu-favorites/{menuId}", 1L)
                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails())))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        // then
        verify(menuFavoriteService).addFavorite(any(), eq(1L));
    }

    @Test
    void removeFavoriteDelegatesToService() throws Exception {
        // when
        mockMvc.perform(delete("/menu-favorites/{menuId}", 1L)
                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails())))
               .andExpect(status().isOk());

        // then
        verify(menuFavoriteService).removeFavorite(any(), eq(1L));
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null);
    }
}
