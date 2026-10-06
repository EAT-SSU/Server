package ssu.eatssu.domain.notification.entity;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.PathInits;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QPushTokenTest {

    @Test
    void exposesQuerydslPathsForPushTokenFields() {
        // given
        QPushToken query = QPushToken.pushToken;

        // when & then
        assertThat(query.getType()).isEqualTo(PushToken.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.user.getMetadata().getName()).isEqualTo("user");
        assertThat(query.token.getMetadata().getName()).isEqualTo("token");
        assertThat(query.deviceType.getMetadata().getName()).isEqualTo("deviceType");
        assertThat(query.createdDate.getMetadata().getName()).isEqualTo("createdDate");
        assertThat(query.modifiedDate.getMetadata().getName()).isEqualTo("modifiedDate");
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        // given
        PathMetadata metadata = QPushToken.pushToken.getMetadata();

        // when & then
        assertThat(new QPushToken("pushToken").getType()).isEqualTo(PushToken.class);
        assertThat(new QPushToken(QPushToken.pushToken).getType()).isEqualTo(PushToken.class);
        assertThat(new QPushToken(metadata).getType()).isEqualTo(PushToken.class);
        assertThat(new QPushToken(metadata, PathInits.DIRECT2).getType()).isEqualTo(PushToken.class);
        assertThat(new QPushToken(PushToken.class, metadata, PathInits.DIRECT2).getType())
                .isEqualTo(PushToken.class);
    }
}
