package ssu.eatssu.domain.menu.entity;

import com.querydsl.core.types.PathMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QMenuCategoryTest {

    @Test
    void exposesQuerydslPathsForMenuCategoryFields() {
        QMenuCategory query = QMenuCategory.menuCategory;

        assertThat(query.getType()).isEqualTo(MenuCategory.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.name.getMetadata().getName()).isEqualTo("name");
        assertThat(query.restaurant.getMetadata().getName()).isEqualTo("restaurant");
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        PathMetadata metadata = QMenuCategory.menuCategory.getMetadata();

        assertThat(new QMenuCategory("menuCategory").getType()).isEqualTo(MenuCategory.class);
        assertThat(new QMenuCategory(QMenuCategory.menuCategory).getType()).isEqualTo(MenuCategory.class);
        assertThat(new QMenuCategory(metadata).getType()).isEqualTo(MenuCategory.class);
    }
}
