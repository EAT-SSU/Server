package ssu.eatssu.domain.menu.entity;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class MenuEntityTest {

    @Test
    void menuTracksDiscontinuedAndLikeStates() {
        Menu menu = Menu.createFixed("돈가스", Restaurant.FOOD_COURT, 6000, null);

        menu.changeDiscontinuedStatus();
        menu.increaseLikeCount();
        menu.changeLikeStatus(false);

        assertThat(menu.isContinued()).isFalse();
        assertThat(menu.getLikeCount()).isZero();
        menu.cancelLike(true);
        assertThat(menu.getLikeCount()).isEqualTo(-1);
    }

    @Test
    void nullLikeCountIsInitializedBeforeUpdates() {
        Menu menu = Menu.createVariable("라면", Restaurant.DODAM);
        ReflectionTestUtils.setField(menu, "likeCount", null);
        ReflectionTestUtils.setField(menu, "unlikeCount", null);

        menu.increaseLikeCount();
        menu.decreaseLikeCount();

        assertThat(menu.getLikeCount()).isEqualTo(0);
    }

    @Test
    void changeLikeStatusIncreasesWhenLiked() {
        Menu menu = Menu.createFixed("돈가스", Restaurant.FOOD_COURT, 6000, null);

        menu.changeLikeStatus(true);

        assertThat(menu.getLikeCount()).isEqualTo(1);
    }

    @Test
    void cancelLikeDoesNothingWhenNotPreviouslyLiked() {
        Menu menu = Menu.createFixed("돈가스", Restaurant.FOOD_COURT, 6000, null);

        menu.cancelLike(false);

        assertThat(menu.getLikeCount()).isZero();
    }

    @Test
    void decreaseLikeCountInitializesNullCountBeforeDecrementing() {
        Menu menu = Menu.createVariable("라면", Restaurant.DODAM);
        ReflectionTestUtils.setField(menu, "likeCount", null);

        menu.decreaseLikeCount();

        assertThat(menu.getLikeCount()).isEqualTo(-1);
    }

    @Test
    void mealReturnsMenusAndNamesInInsertionOrder() {
        Date date = new Date();
        Menu first = Menu.createVariable("라면", Restaurant.DODAM);
        Menu second = Menu.createVariable("김치", Restaurant.DODAM);
        Meal meal = new Meal(date, TimePart.LUNCH, Restaurant.DODAM);
        meal.addMealMenu(MealMenu.builder().meal(meal).menu(first).build());
        meal.addMealMenu(MealMenu.builder().meal(meal).menu(second).build());

        assertThat(meal.getMenus()).containsExactly(first, second);
        assertThat(meal.getMenuNames()).containsExactly("라면", "김치");
        assertThat(meal.getDate()).isEqualTo(date);
        assertThat(meal.getId()).isNull();
        assertThat(meal.getMealMenus()).hasSize(2);
    }

    @Test
    void mealMenuLinksMealAndMenu() {
        Menu menu = Menu.createVariable("라면", Restaurant.DODAM);
        Meal meal = new Meal(new Date(), TimePart.LUNCH, Restaurant.DODAM);

        MealMenu mealMenu = MealMenu.builder().meal(meal).menu(menu).build();

        assertThat(mealMenu.getMeal()).isSameAs(meal);
        assertThat(mealMenu.getMenu()).isSameAs(menu);
        assertThat(mealMenu.getId()).isNull();
    }
}
