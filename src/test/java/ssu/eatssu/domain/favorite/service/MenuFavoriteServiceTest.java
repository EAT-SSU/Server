package ssu.eatssu.domain.favorite.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.favorite.dto.response.FavoriteMenuResponse;
import ssu.eatssu.domain.favorite.dto.response.MenuSearchResponse;
import ssu.eatssu.domain.favorite.entity.MenuFavorite;
import ssu.eatssu.domain.favorite.persistence.MenuFavoriteQueryRepository;
import ssu.eatssu.domain.favorite.persistence.MenuFavoriteRepository;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MenuFavoriteServiceTest {

    private static final Long USER_ID = 1L;

    private final MenuFavoriteRepository menuFavoriteRepository = mock(MenuFavoriteRepository.class);
    private final MenuFavoriteQueryRepository menuFavoriteQueryRepository = mock(MenuFavoriteQueryRepository.class);
    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final MenuFavoriteService service = new MenuFavoriteService(menuFavoriteRepository,
                                                                        menuFavoriteQueryRepository,
                                                                        menuRepository, userRepository);
    private final CustomUserDetails userDetails = new CustomUserDetails(USER_ID, "user@eatssu.com", "credentials",
                                                                        Role.USER, null);
    private User user;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        when(user.getId()).thenReturn(USER_ID);
        when(user.getLanguage()).thenReturn(Language.KO);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    }

    @Test
    void searchRejectsKeywordShorterThanTwoCharactersAfterTrimming() {
        // given
        String keyword = "  돈 ";

        // when & then
        assertThatThrownBy(() -> service.searchMenus(userDetails, keyword))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.INVALID_SEARCH_KEYWORD);
        assertThatThrownBy(() -> service.searchMenus(userDetails, null))
                .isInstanceOf(BaseException.class);
        verifyNoInteractions(menuFavoriteQueryRepository);
    }

    @Test
    void searchOrdersExactThenPrefixThenPartialAndRemovesDuplicates() {
        // given
        Menu stew = menu(1L, "김치찌개", Restaurant.HAKSIK);
        Menu kimchi = menu(2L, "김치", Restaurant.DODAM);
        Menu friedRice = menu(3L, "돼지김치볶음", Restaurant.HAKSIK);
        Menu duplicatedKimchi = menu(4L, "김치", Restaurant.DODAM);
        Menu kimchiAtOtherRestaurant = menu(5L, "김치", Restaurant.HAKSIK);
        when(menuFavoriteQueryRepository.searchMenus("김치", Language.KO))
                .thenReturn(List.of(friedRice, duplicatedKimchi, stew, kimchiAtOtherRestaurant, kimchi));
        when(menuFavoriteRepository.findFavoriteMenuIds(eq(USER_ID), any())).thenReturn(Set.of(1L));

        // when
        List<MenuSearchResponse> responses = service.searchMenus(userDetails, " 김치 ");

        // then
        assertThat(responses).extracting(MenuSearchResponse::menuId).containsExactly(2L, 5L, 1L, 3L);
        assertThat(responses).extracting(MenuSearchResponse::isFavorite).containsExactly(false, false, true, false);
        assertThat(responses.get(0).restaurant()).isEqualTo(Restaurant.DODAM);
    }

    @Test
    void searchIsCaseInsensitiveAndUsesTranslatedNameForForeignUsers() {
        // given
        when(user.getLanguage()).thenReturn(Language.EN);
        Menu cutlet = menu(1L, "돈까스", Restaurant.HAKSIK);
        ReflectionTestUtils.setField(cutlet, "nameEn", "Pork Cutlet");
        Menu untranslated = menu(2L, "치즈 PORK", Restaurant.DODAM);
        when(menuFavoriteQueryRepository.searchMenus("pork", Language.EN)).thenReturn(List.of(untranslated, cutlet));
        when(menuFavoriteRepository.findFavoriteMenuIds(eq(USER_ID), any())).thenReturn(Set.of());

        // when
        List<MenuSearchResponse> responses = service.searchMenus(userDetails, "pork");

        // then
        assertThat(responses).extracting(MenuSearchResponse::menuName).containsExactly("Pork Cutlet", "치즈 PORK");
    }

    @Test
    void searchReturnsEmptyListWithoutCheckingFavoritesWhenNothingMatches() {
        // given
        when(menuFavoriteQueryRepository.searchMenus("없는메뉴", Language.KO)).thenReturn(List.of());

        // when
        List<MenuSearchResponse> responses = service.searchMenus(userDetails, "없는메뉴");

        // then
        assertThat(responses).isEmpty();
        verify(menuFavoriteRepository, never()).findFavoriteMenuIds(anyLong(), any());
    }

    @Test
    void searchThrowsWhenUserDoesNotExist() {
        // given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.searchMenus(userDetails, "김치"))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.NOT_FOUND_USER);
    }

    @Test
    void getFavoriteMenusKeepsRepositoryOrderAndShowsDiscontinuedMenus() {
        // given
        Menu latest = menu(1L, "파스타", Restaurant.DODAM);
        Menu discontinued = menu(2L, "돈까스", Restaurant.HAKSIK);
        discontinued.changeDiscontinuedStatus();
        when(menuFavoriteRepository.findAllWithMenuByUserId(USER_ID))
                .thenReturn(List.of(MenuFavorite.of(user, latest), MenuFavorite.of(user, discontinued)));

        // when
        List<FavoriteMenuResponse> responses = service.getFavoriteMenus(userDetails);

        // then
        assertThat(responses).extracting(FavoriteMenuResponse::menuId).containsExactly(1L, 2L);
        assertThat(responses).extracting(FavoriteMenuResponse::isDiscontinued).containsExactly(false, true);
        assertThat(responses.get(0).menuName()).isEqualTo("파스타");
    }

    @Test
    void addFavoriteSavesNewFavorite() {
        // given
        Menu menu = menu(10L, "파스타", Restaurant.DODAM);
        when(menuRepository.findById(10L)).thenReturn(Optional.of(menu));
        when(menuFavoriteRepository.existsByUserIdAndMenuId(USER_ID, 10L)).thenReturn(false);

        // when
        service.addFavorite(userDetails, 10L);

        // then
        verify(menuFavoriteRepository).save(any(MenuFavorite.class));
    }

    @Test
    void addFavoriteDoesNothingWhenAlreadyFavorited() {
        // given
        Menu menu = menu(10L, "파스타", Restaurant.DODAM);
        menu.changeDiscontinuedStatus();
        when(menuRepository.findById(10L)).thenReturn(Optional.of(menu));
        when(menuFavoriteRepository.existsByUserIdAndMenuId(USER_ID, 10L)).thenReturn(true);

        // when
        service.addFavorite(userDetails, 10L);

        // then
        verify(menuFavoriteRepository, never()).save(any());
    }

    @Test
    void addFavoriteRejectsDiscontinuedMenu() {
        // given
        Menu menu = menu(10L, "파스타", Restaurant.DODAM);
        menu.changeDiscontinuedStatus();
        when(menuRepository.findById(10L)).thenReturn(Optional.of(menu));

        // when & then
        assertThatThrownBy(() -> service.addFavorite(userDetails, 10L))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.DISCONTINUED_MENU);
        verify(menuFavoriteRepository, never()).save(any());
    }

    @Test
    void addFavoriteThrowsWhenMenuDoesNotExist() {
        // given
        when(menuRepository.findById(10L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.addFavorite(userDetails, 10L))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.NOT_FOUND_MENU);
    }

    @Test
    void removeFavoriteDeletesByUserAndMenu() {
        // given
        Long menuId = 10L;

        // when
        service.removeFavorite(userDetails, menuId);

        // then
        verify(menuFavoriteRepository).deleteByUserIdAndMenuId(USER_ID, menuId);
    }

    private Menu menu(Long id, String name, Restaurant restaurant) {
        Menu menu = Menu.createVariable(name, restaurant);
        ReflectionTestUtils.setField(menu, "id", id);
        return menu;
    }
}
