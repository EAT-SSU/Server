package ssu.eatssu.domain.menu.entity;

import com.querydsl.core.types.PathMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QMealTest {

    @Test
    void exposesQuerydslPathsForMealFields() {
        QMeal query = QMeal.meal;

        assertThat(query.getType()).isEqualTo(Meal.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.date.getMetadata().getName()).isEqualTo("date");
        assertThat(query.price.getMetadata().getName()).isEqualTo("price");
        assertThat(query.restaurant.getMetadata().getName()).isEqualTo("restaurant");
        assertThat(query.timePart.getMetadata().getName()).isEqualTo("timePart");
        assertThat(query.mealMenus.getMetadata().getName()).isEqualTo("mealMenus");
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        PathMetadata metadata = QMeal.meal.getMetadata();

        assertThat(new QMeal("meal").getType()).isEqualTo(Meal.class);
        assertThat(new QMeal(QMeal.meal).getType()).isEqualTo(Meal.class);
        assertThat(new QMeal(metadata).getType()).isEqualTo(Meal.class);
    }
}
