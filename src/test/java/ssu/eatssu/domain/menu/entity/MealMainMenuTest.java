package ssu.eatssu.domain.menu.entity;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class MealMainMenuTest {

    @Test
    void ofBuildsMealMainMenuWithGivenFields() {
        Meal meal = new Meal(new Date(), TimePart.LUNCH, Restaurant.DODAM);

        MealMainMenu mealMainMenu = MealMainMenu.of(meal, 0, "돈까스", "Pork Cutlet");

        assertThat(mealMainMenu.getMeal()).isSameAs(meal);
        assertThat(mealMainMenu.getSeq()).isZero();
        assertThat(mealMainMenu.getNameKo()).isEqualTo("돈까스");
        assertThat(mealMainMenu.getNameEn()).isEqualTo("Pork Cutlet");
        assertThat(mealMainMenu.getId()).isNull();
    }
}
