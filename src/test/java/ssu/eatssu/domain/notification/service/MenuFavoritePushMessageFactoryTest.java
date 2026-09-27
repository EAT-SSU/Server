package ssu.eatssu.domain.notification.service;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.notification.infrastructure.PushMessage;
import ssu.eatssu.domain.notification.persistence.MenuFavoriteMatch;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MenuFavoritePushMessageFactoryTest {

    private static final LocalDate DATE = LocalDate.of(2026, 9, 28);

    private final MenuFavoritePushMessageFactory factory = new MenuFavoritePushMessageFactory();

    @Test
    void singleMenuAtSingleRestaurant() {
        // given
        List<MenuFavoriteMatch> matches = List.of(match("파스타", Restaurant.HAKSIK, TimePart.LUNCH));

        // when
        PushMessage message = factory.create(DATE, matches);

        // then
        assertThat(message.title()).isEqualTo("🍽️ 찜한 메뉴가 나온대요!");
        assertThat(message.body()).isEqualTo("오늘 점심, 학생식당에 파스타 등장 👀");
        assertThat(message.data()).containsEntry("type", "MENU_FAVORITE")
                                  .containsEntry("date", "20260928")
                                  .containsEntry("timePart", "LUNCH");
    }

    @Test
    void sameMenuAtSeveralRestaurants() {
        // given
        List<MenuFavoriteMatch> matches = List.of(
                match("파스타", Restaurant.HAKSIK, TimePart.LUNCH),
                match("파스타", Restaurant.DORMITORY, TimePart.LUNCH),
                match("파스타", Restaurant.DODAM, TimePart.LUNCH));

        // when
        PushMessage message = factory.create(DATE, matches);

        // then
        assertThat(message.title()).isEqualTo("🍽️ 찜한 메뉴가 여러 곳에 나온대요!");
        assertThat(message.body()).isEqualTo("오늘 점심, 도담식당 외 2곳에 파스타 등장 👀");
    }

    @Test
    void twoDifferentMenus() {
        // given
        List<MenuFavoriteMatch> matches = List.of(
                match("돈가스", Restaurant.HAKSIK, TimePart.LUNCH),
                match("파스타", Restaurant.DODAM, TimePart.LUNCH));

        // when
        PushMessage message = factory.create(DATE, matches);

        // then
        assertThat(message.title()).isEqualTo("🍽️ 찜한 메뉴가 2개나 나온대요!");
        assertThat(message.body()).isEqualTo("오늘 점심, 도담식당 파스타 · 학생식당 돈가스 등장 👀");
    }

    @Test
    void threeOrMoreDifferentMenus() {
        // given
        List<MenuFavoriteMatch> matches = List.of(
                match("돈가스", Restaurant.HAKSIK, TimePart.LUNCH),
                match("파스타", Restaurant.DODAM, TimePart.LUNCH),
                match("김치찌개", Restaurant.FACULTY, TimePart.LUNCH));

        // when
        PushMessage message = factory.create(DATE, matches);

        // then
        assertThat(message.title()).isEqualTo("🍽️ 찜한 메뉴가 여러 개 나온대요!");
        assertThat(message.body()).isEqualTo("오늘 점심, 도담식당 파스타 외 2개 등장 👀");
    }

    @Test
    void lunchAndDinnerMatchesShareOneMessageAndLinkToLunch() {
        // given
        List<MenuFavoriteMatch> matches = List.of(
                match("파스타", Restaurant.HAKSIK, TimePart.DINNER),
                match("파스타", Restaurant.HAKSIK, TimePart.LUNCH));

        // when
        PushMessage message = factory.create(DATE, matches);

        // then
        assertThat(message.title()).isEqualTo("🍽️ 찜한 메뉴가 나온대요!");
        assertThat(message.body()).isEqualTo("오늘 점심·저녁, 학생식당에 파스타 등장 👀");
        assertThat(message.data()).containsEntry("timePart", "LUNCH");
    }

    @Test
    void dinnerOnlyMatchesUseDinnerLabel() {
        // given
        List<MenuFavoriteMatch> matches = List.of(match("라면", Restaurant.DORMITORY, TimePart.DINNER));

        // when
        PushMessage message = factory.create(DATE, matches);

        // then
        assertThat(message.body()).isEqualTo("오늘 저녁, 기숙사식당에 라면 등장 👀");
        assertThat(message.data()).containsEntry("timePart", "DINNER");
    }

    @Test
    void fixedMenuRestaurantsAndMorningHaveDisplayNames() {
        // given
        List<MenuFavoriteMatch> matches = List.of(
                match("김밥", Restaurant.SNACK_CORNER, TimePart.MORNING),
                match("김밥", Restaurant.FOOD_COURT, TimePart.MORNING));

        // when
        PushMessage message = factory.create(DATE, matches);

        // then
        assertThat(message.body()).isEqualTo("오늘 아침, 푸드코트 외 1곳에 김밥 등장 👀");
    }

    private MenuFavoriteMatch match(String menuName, Restaurant restaurant, TimePart timePart) {
        return new MenuFavoriteMatch(1L, menuName, restaurant, timePart);
    }
}
