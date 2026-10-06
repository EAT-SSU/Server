package ssu.eatssu.domain.favorite.entity;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.PathInits;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QMenuFavoriteTest {

    @Test
    void exposesQuerydslPathsForMenuFavoriteFields() {
        // given
        QMenuFavorite query = QMenuFavorite.menuFavorite;

        // when & then
        assertThat(query.getType()).isEqualTo(MenuFavorite.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.user.getMetadata().getName()).isEqualTo("user");
        assertThat(query.menu.getMetadata().getName()).isEqualTo("menu");
        assertThat(query.createdDate.getMetadata().getName()).isEqualTo("createdDate");
        assertThat(query.modifiedDate.getMetadata().getName()).isEqualTo("modifiedDate");
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        // given
        PathMetadata metadata = QMenuFavorite.menuFavorite.getMetadata();

        // when & then
        assertThat(new QMenuFavorite("menuFavorite").getType()).isEqualTo(MenuFavorite.class);
        assertThat(new QMenuFavorite(QMenuFavorite.menuFavorite).getType()).isEqualTo(MenuFavorite.class);
        assertThat(new QMenuFavorite(metadata).getType()).isEqualTo(MenuFavorite.class);
        assertThat(new QMenuFavorite(metadata, PathInits.DIRECT2).getType()).isEqualTo(MenuFavorite.class);
        assertThat(new QMenuFavorite(MenuFavorite.class, metadata, PathInits.DIRECT2).getType())
                .isEqualTo(MenuFavorite.class);
    }
}
