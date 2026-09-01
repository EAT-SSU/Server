package ssu.eatssu.domain.menu.presentation.dto.response;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.user.entity.Language;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class MenuResponseDtoTest {

    @Test
    void briefMenuUsesTranslationOnlyForMainMenu() {
        Menu menu = mock(Menu.class);
        given(menu.getId()).willReturn(1L);
        given(menu.getName()).willReturn("돈가스");

        BriefMenuResponse translated = new BriefMenuResponse(menu, Map.of("돈가스", "Pork cutlet"));
        BriefMenuResponse original = new BriefMenuResponse(menu, Map.of());

        assertThat(translated.getName()).isEqualTo("Pork cutlet");
        assertThat(translated.isMain()).isTrue();
        assertThat(translated.getMenuId()).isEqualTo(1L);
        assertThat(original.getName()).isEqualTo("돈가스");
        assertThat(original.isMain()).isFalse();

        BriefMenuResponse withoutTranslations = new BriefMenuResponse(menu);
        assertThat(withoutTranslations.getName()).isEqualTo("돈가스");
        assertThat(withoutTranslations.isMain()).isFalse();
    }

    @Test
    void menuResponsesMapLocalizedNameAndCollectCategories() {
        Menu menu = mock(Menu.class);
        given(menu.getId()).willReturn(1L);
        given(menu.getNameByLanguage(Language.EN)).willReturn("Pork cutlet");
        given(menu.getPrice()).willReturn(6000);
        MenuResponse response = MenuResponse.from(menu, 4.5, Language.EN);
        CategoryWithMenusResponse category = CategoryWithMenusResponse.of("한식", List.of(response));
        MenuRestaurantResponse restaurant = MenuRestaurantResponse.init();
        restaurant.add(category);
        restaurant.addAll(List.of(category));

        assertThat(response.getMenuId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Pork cutlet");
        assertThat(response.getPrice()).isEqualTo(6000);
        assertThat(response.getRating()).isEqualTo(4.5);
        assertThat(category.getCategory()).isEqualTo("한식");
        assertThat(category.getMenus()).containsExactly(response);
        assertThat(restaurant.getCategoryMenuListCollection()).containsExactly(category, category);
    }

    @Test
    void menusInMealMapsAllBriefMenus() {
        Menu menu = mock(Menu.class);
        given(menu.getId()).willReturn(1L);
        given(menu.getName()).willReturn("라면");

        MenusInMealResponse response = MenusInMealResponse.from(List.of(menu), Map.of());

        assertThat(response.getBriefMenus()).extracting(BriefMenuResponse::getName).containsExactly("라면");
        assertThat(new MenusInMealResponse().getBriefMenus()).isNull();
        assertThat(new MealDetailResponse().getMealId()).isNull();
    }
}
