package ssu.eatssu.domain.notification.entity;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.PathInits;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QMenuFavoritePushHistoryTest {

    @Test
    void exposesQuerydslPathsForPushHistoryFields() {
        // given
        QMenuFavoritePushHistory query = QMenuFavoritePushHistory.menuFavoritePushHistory;

        // when & then
        assertThat(query.getType()).isEqualTo(MenuFavoritePushHistory.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.user.getMetadata().getName()).isEqualTo("user");
        assertThat(query.pushDate.getMetadata().getName()).isEqualTo("pushDate");
        assertThat(query.createdDate.getMetadata().getName()).isEqualTo("createdDate");
        assertThat(query.modifiedDate.getMetadata().getName()).isEqualTo("modifiedDate");
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        // given
        PathMetadata metadata = QMenuFavoritePushHistory.menuFavoritePushHistory.getMetadata();

        // when & then
        assertThat(new QMenuFavoritePushHistory("menuFavoritePushHistory").getType())
                .isEqualTo(MenuFavoritePushHistory.class);
        assertThat(new QMenuFavoritePushHistory(QMenuFavoritePushHistory.menuFavoritePushHistory).getType())
                .isEqualTo(MenuFavoritePushHistory.class);
        assertThat(new QMenuFavoritePushHistory(metadata).getType()).isEqualTo(MenuFavoritePushHistory.class);
        assertThat(new QMenuFavoritePushHistory(metadata, PathInits.DIRECT2).getType())
                .isEqualTo(MenuFavoritePushHistory.class);
        assertThat(new QMenuFavoritePushHistory(MenuFavoritePushHistory.class, metadata, PathInits.DIRECT2).getType())
                .isEqualTo(MenuFavoritePushHistory.class);
    }
}
