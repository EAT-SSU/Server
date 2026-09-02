package ssu.eatssu.domain.menu.entity;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.PathInits;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QMealMenuTest {

    @Test
    void exposesQuerydslPathsForMealMenuFields() {
        QMealMenu query = QMealMenu.mealMenu;

        assertThat(query.getType()).isEqualTo(MealMenu.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.meal).isNotNull();
        assertThat(query.menu).isNotNull();
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        PathMetadata metadata = QMealMenu.mealMenu.getMetadata();

        assertThat(new QMealMenu("mealMenu").getType()).isEqualTo(MealMenu.class);
        assertThat(new QMealMenu(QMealMenu.mealMenu).getType()).isEqualTo(MealMenu.class);
        assertThat(new QMealMenu(metadata).getType()).isEqualTo(MealMenu.class);
        assertThat(new QMealMenu(metadata, PathInits.DIRECT2).getType()).isEqualTo(MealMenu.class);
        assertThat(new QMealMenu(MealMenu.class, metadata, PathInits.DIRECT2).getType()).isEqualTo(MealMenu.class);
    }
}
