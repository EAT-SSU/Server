package ssu.eatssu.domain.menu.presentation.dto;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.presentation.dto.request.CreateMealRequest;
import ssu.eatssu.domain.menu.presentation.dto.request.MainMenuRequest;
import ssu.eatssu.domain.menu.presentation.dto.request.MealCreateWithPriceRequest;
import ssu.eatssu.domain.menu.presentation.dto.response.MealCreateResult;
import ssu.eatssu.domain.menu.presentation.dto.response.MealDetailResponse;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class MenuPresentationDtoTest {

    @Test
    void requestRecordsExposeSubmittedValues() {
        MainMenuRequest mainMenu = new MainMenuRequest("돈가스", "Pork cutlet");
        CreateMealRequest create = new CreateMealRequest(List.of("돈가스"), List.of(mainMenu));
        MealCreateWithPriceRequest priced = new MealCreateWithPriceRequest(List.of("돈가스"), 6000,
                                                                            List.of(mainMenu));

        assertThat(create.menuNames()).containsExactly("돈가스");
        assertThat(create.mainMenus()).containsExactly(mainMenu);
        assertThat(mainMenu.nameKo()).isEqualTo("돈가스");
        assertThat(mainMenu.nameEn()).isEqualTo("Pork cutlet");
        assertThat(priced.menuNames()).containsExactly("돈가스");
        assertThat(priced.price()).isEqualTo(6000);
        assertThat(priced.mainMenus()).containsExactly(mainMenu);
    }

    @Test
    void mealDetailMapsMealMenusAndMainMenuTranslations() {
        Menu menu = mock(Menu.class);
        given(menu.getId()).willReturn(2L);
        given(menu.getName()).willReturn("돈가스");
        MealMenu mealMenu = MealMenu.builder().menu(menu).build();
        Meal meal = mock(Meal.class);
        given(meal.getId()).willReturn(10L);
        given(meal.getPrice()).willReturn(6000);
        given(meal.getMealMenus()).willReturn(List.of(mealMenu));

        MealDetailResponse response = MealDetailResponse.from(meal, 4.5, Map.of("돈가스", "Pork cutlet"));

        assertThat(response.getMealId()).isEqualTo(10L);
        assertThat(response.getPrice()).isEqualTo(6000);
        assertThat(response.getRating()).isEqualTo(4.5);
        assertThat(response.getBriefMenus()).hasSize(1);
        assertThat(response.getBriefMenus().get(0).getName()).isEqualTo("Pork cutlet");
        assertThat(response.getBriefMenus().get(0).isMain()).isTrue();
    }

    @Test
    void mealCreateResultStoresMealIdAndUnmatchedMenus() {
        MealCreateResult result = new MealCreateResult(10L, List.of("없는 메뉴"));

        assertThat(result.mealId()).isEqualTo(10L);
        assertThat(result.unmatchedMainMenus()).containsExactly("없는 메뉴");
    }
}
