package ssu.eatssu.domain.menu.entity;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.PathInits;
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
        assertThat(query.createdDate.getMetadata().getName()).isEqualTo("createdDate");
        assertThat(query.modifiedDate.getMetadata().getName()).isEqualTo("modifiedDate");
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        PathMetadata metadata = QMealMainMenu.mealMainMenu.getMetadata();

        assertThat(new QMealMainMenu("mealMainMenu").getType()).isEqualTo(MealMainMenu.class);
        assertThat(new QMealMainMenu(QMealMainMenu.mealMainMenu).getType()).isEqualTo(MealMainMenu.class);
        assertThat(new QMealMainMenu(metadata).getType()).isEqualTo(MealMainMenu.class);
        assertThat(new QMealMainMenu(metadata, PathInits.DIRECT2).getType()).isEqualTo(MealMainMenu.class);
        assertThat(new QMealMainMenu(MealMainMenu.class, metadata, PathInits.DIRECT2).getType())
                .isEqualTo(MealMainMenu.class);
    }
}
