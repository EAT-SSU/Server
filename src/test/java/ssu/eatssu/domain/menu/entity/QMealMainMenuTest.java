package ssu.eatssu.domain.menu.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QMealMainMenuTest {

    @Test
    void exposesQuerydslPathsForMealMainMenuFields() {
        QMealMainMenu query = QMealMainMenu.mealMainMenu;

        assertThat(query.getType()).isEqualTo(MealMainMenu.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.meal.getMetadata().getName()).isEqualTo("meal");
        assertThat(query.seq.getMetadata().getName()).isEqualTo("seq");
        assertThat(query.nameKo.getMetadata().getName()).isEqualTo("nameKo");
        assertThat(query.nameEn.getMetadata().getName()).isEqualTo("nameEn");
    }
}
