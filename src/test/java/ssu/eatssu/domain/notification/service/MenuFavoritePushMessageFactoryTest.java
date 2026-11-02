package ssu.eatssu.domain.notification.service;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.notification.infrastructure.PushMessage;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.Language;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MenuFavoritePushMessageFactoryTest {

    private static final LocalDate DATE = LocalDate.of(2026, 9, 28);

    private final MenuFavoritePushMessageFactory factory = new MenuFavoritePushMessageFactory();

    @Test
    void singleMenuInKoreanKeepsExistingDataKeysAndAddsNotificationId() {
        // given
        List<MenuFavoriteItem> items = List.of(item("파스타", "Pasta", Restaurant.HAKSIK, TimePart.LUNCH));

        // when
        PushMessage message = factory.createPush(DATE, items, Language.KO, 7L);

        // then
        assertThat(message.title()).isEqualTo("🍽️ 찜한 메뉴가 나온대요!");
        assertThat(message.body()).isEqualTo("오늘 점심, 학생식당에 파스타 등장 👀");
        assertThat(message.data()).containsEntry("type", "MENU_FAVORITE")
                                  .containsEntry("date", "20260928")
                                  .containsEntry("timePart", "LUNCH")
                                  .containsEntry("notificationId", "7");
    }

    @Test
    void twoDifferentMenusInKorean() {
        // given
        List<MenuFavoriteItem> items = List.of(
                item("돈가스", null, Restaurant.HAKSIK, TimePart.LUNCH),
                item("파스타", null, Restaurant.DODAM, TimePart.LUNCH));

        // when
        NotificationText text = factory.createText(DATE, DATE, items, Language.KO);

        // then
        assertThat(text.title()).isEqualTo("🍽️ 찜한 메뉴가 2개나 나온대요!");
        assertThat(text.body()).isEqualTo("오늘 점심, 도담식당 파스타 · 학생식당 돈가스 등장 👀");
    }

    @Test
    void sameMenuAtSeveralRestaurantsCountsEachRestaurant() {
        // given
        List<MenuFavoriteItem> items = List.of(
                item("파스타", null, Restaurant.HAKSIK, TimePart.LUNCH),
                item("파스타", null, Restaurant.DORMITORY, TimePart.LUNCH),
                item("파스타", null, Restaurant.DODAM, TimePart.LUNCH));

        // when
        NotificationText text = factory.createText(DATE, DATE, items, Language.KO);

        // then
        assertThat(text.title()).isEqualTo("🍽️ 찜한 메뉴가 여러 개 나온대요!");
        assertThat(text.body()).isEqualTo("오늘 점심, 도담식당 파스타 외 2개 등장 👀");
    }

    @Test
    void lunchAndDinnerMatchesShareOneMessageAndLinkToLunch() {
        // given
        List<MenuFavoriteItem> items = List.of(
                item("파스타", null, Restaurant.HAKSIK, TimePart.DINNER),
                item("파스타", null, Restaurant.HAKSIK, TimePart.LUNCH));

        // when
        PushMessage message = factory.createPush(DATE, items, Language.KO, 1L);

        // then
        assertThat(message.title()).isEqualTo("🍽️ 찜한 메뉴가 나온대요!");
        assertThat(message.body()).isEqualTo("오늘 점심·저녁, 학생식당에 파스타 등장 👀");
        assertThat(message.data()).containsEntry("timePart", "LUNCH");
    }

    @Test
    void englishUsesEnglishMenuNameAndFallsBackToKorean() {
        // given
        List<MenuFavoriteItem> items = List.of(
                item("연탄불고기", "Charcoal Bulgogi", Restaurant.HAKSIK, TimePart.LUNCH),
                item("배추김치", null, Restaurant.DODAM, TimePart.LUNCH));

        // when
        NotificationText text = factory.createText(DATE, DATE, items, Language.EN);

        // then
        assertThat(text.title()).isEqualTo("🍽️ Two favorite menus are available!");
        assertThat(text.body())
                .isEqualTo("Today’s Lunch: 배추김치 at Dodam cafeteria · Charcoal Bulgogi at Student cafeteria 👀");
    }

    @Test
    void japaneseAndVietnameseUseEnglishMenuName() {
        // given
        List<MenuFavoriteItem> items = List.of(item("연탄불고기", "Charcoal Bulgogi", Restaurant.HAKSIK,
                                                    TimePart.DINNER));

        // when
        NotificationText japanese = factory.createText(DATE, DATE, items, Language.JA);
        NotificationText vietnamese = factory.createText(DATE, DATE, items, Language.VI);

        // then
        assertThat(japanese.title()).isEqualTo("🍽️ お気に入りメニューが登場！");
        assertThat(japanese.body()).isEqualTo("今日の夕食、学生食堂にCharcoal Bulgogiが登場 👀");
        assertThat(vietnamese.title()).isEqualTo("🍽️ Có món ăn yêu thích hôm nay!");
        assertThat(vietnamese.body()).isEqualTo("Bữa tối hôm nay: Charcoal Bulgogi tại Nhà ăn sinh viên 👀");
    }

    @Test
    void manyMenusInEnglish() {
        // given
        List<MenuFavoriteItem> items = List.of(
                item("돈가스", "Pork Cutlet", Restaurant.HAKSIK, TimePart.LUNCH),
                item("파스타", "Pasta", Restaurant.DODAM, TimePart.LUNCH),
                item("김치찌개", null, Restaurant.FACULTY, TimePart.LUNCH));

        // when
        NotificationText text = factory.createText(DATE, DATE, items, Language.EN);

        // then
        assertThat(text.title()).isEqualTo("🍽️ Several favorite menus are available!");
        assertThat(text.body()).isEqualTo("Today’s Lunch: Pasta at Dodam cafeteria and 2 more 👀");
    }

    @Test
    void pastNotificationsShowDateInsteadOfToday() {
        // given
        LocalDate today = DATE.plusDays(3);
        List<MenuFavoriteItem> items = List.of(item("연탄불고기", "Charcoal Bulgogi", Restaurant.HAKSIK,
                                                    TimePart.LUNCH));

        // when & then
        assertThat(factory.createText(DATE, today, items, Language.KO).body())
                .isEqualTo("9월 28일 점심, 학생식당에 연탄불고기 등장 👀");
        assertThat(factory.createText(DATE, today, items, Language.EN).body())
                .isEqualTo("Lunch on Sep 28: Charcoal Bulgogi at Student cafeteria 👀");
        assertThat(factory.createText(DATE, today, items, Language.JA).body())
                .isEqualTo("9月28日の昼食、学生食堂にCharcoal Bulgogiが登場 👀");
        assertThat(factory.createText(DATE, today, items, Language.VI).body())
                .isEqualTo("Bữa trưa ngày 28/9: Charcoal Bulgogi tại Nhà ăn sinh viên 👀");
    }

    @Test
    void nullLanguageFallsBackToKorean() {
        // given
        List<MenuFavoriteItem> items = List.of(item("라면", "Ramen", Restaurant.DORMITORY, TimePart.DINNER));

        // when
        NotificationText text = factory.createText(DATE, DATE, items, null);

        // then
        assertThat(text.body()).isEqualTo("오늘 저녁, 기숙사식당에 라면 등장 👀");
    }

    @Test
    void everyRestaurantAndTimePartHasDisplayNameInEveryLanguage() {
        for (Language language : Language.values()) {
            for (Restaurant restaurant : Restaurant.values()) {
                for (TimePart timePart : TimePart.values()) {
                    // given
                    List<MenuFavoriteItem> items = List.of(item("김밥", null, restaurant, timePart));

                    // when
                    NotificationText text = factory.createText(DATE, DATE, items, language);

                    // then
                    assertThat(text.body()).doesNotContain("null");
                }
            }
        }
    }

    private MenuFavoriteItem item(String menuName, String menuNameEn, Restaurant restaurant, TimePart timePart) {
        return new MenuFavoriteItem(menuName, menuNameEn, restaurant, timePart);
    }
}
