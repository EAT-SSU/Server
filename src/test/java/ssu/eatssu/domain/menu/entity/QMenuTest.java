package ssu.eatssu.domain.menu.entity;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.PathInits;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QMenuTest {

    @Test
    void exposesQuerydslPathsForMenuFields() {
        QMenu query = QMenu.menu;

        assertThat(query.getType()).isEqualTo(Menu.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.name.getMetadata().getName()).isEqualTo("name");
        assertThat(query.nameEn.getMetadata().getName()).isEqualTo("nameEn");
        assertThat(query.nameJa.getMetadata().getName()).isEqualTo("nameJa");
        assertThat(query.nameVi.getMetadata().getName()).isEqualTo("nameVi");
        assertThat(query.price.getMetadata().getName()).isEqualTo("price");
        assertThat(query.restaurant.getMetadata().getName()).isEqualTo("restaurant");
        assertThat(query.isDiscontinued.getMetadata().getName()).isEqualTo("isDiscontinued");
        assertThat(query.likeCount.getMetadata().getName()).isEqualTo("likeCount");
        assertThat(query.unlikeCount.getMetadata().getName()).isEqualTo("unlikeCount");
        assertThat(query.mealMenus.getMetadata().getName()).isEqualTo("mealMenus");
        assertThat(query.category).isNotNull();
        assertThat(query.reviews).isNotNull();
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        PathMetadata metadata = QMenu.menu.getMetadata();

        assertThat(new QMenu("menu").getType()).isEqualTo(Menu.class);
        assertThat(new QMenu(QMenu.menu).getType()).isEqualTo(Menu.class);
        assertThat(new QMenu(metadata).getType()).isEqualTo(Menu.class);
        assertThat(new QMenu(metadata, PathInits.DIRECT2).getType()).isEqualTo(Menu.class);
        assertThat(new QMenu(Menu.class, metadata, PathInits.DIRECT2).getType()).isEqualTo(Menu.class);
    }
}
