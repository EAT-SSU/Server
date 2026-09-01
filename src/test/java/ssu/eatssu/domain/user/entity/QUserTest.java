package ssu.eatssu.domain.user.entity;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.PathInits;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QUserTest {

    @Test
    void exposesQuerydslPathsForUserFields() {
        QUser query = QUser.user;

        assertThat(query.getType()).isEqualTo(User.class);
        assertThat(query.id.getMetadata().getName()).isEqualTo("id");
        assertThat(query.email.getMetadata().getName()).isEqualTo("email");
        assertThat(query.nickname.getMetadata().getName()).isEqualTo("nickname");
        assertThat(query.credentials.getMetadata().getName()).isEqualTo("credentials");
        assertThat(query.provider.getMetadata().getName()).isEqualTo("provider");
        assertThat(query.providerId.getMetadata().getName()).isEqualTo("providerId");
        assertThat(query.role.getMetadata().getName()).isEqualTo("role");
        assertThat(query.status.getMetadata().getName()).isEqualTo("status");
        assertThat(query.language.getMetadata().getName()).isEqualTo("language");
        assertThat(query.deviceType.getMetadata().getName()).isEqualTo("deviceType");
        assertThat(query.createdDate.getMetadata().getName()).isEqualTo("createdDate");
        assertThat(query.modifiedDate.getMetadata().getName()).isEqualTo("modifiedDate");
        assertThat(query.reviews.getMetadata().getName()).isEqualTo("reviews");
        assertThat(query.reviewLikes.getMetadata().getName()).isEqualTo("reviewLikes");
        assertThat(query.reviewReports.getMetadata().getName()).isEqualTo("reviewReports");
        assertThat(query.partnershipLikes.getMetadata().getName()).isEqualTo("partnershipLikes");
        assertThat(query.userInquiries.getMetadata().getName()).isEqualTo("userInquiries");
        assertThat(query.department).isNotNull();
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        PathMetadata metadata = QUser.user.getMetadata();

        assertThat(new QUser("user").getType()).isEqualTo(User.class);
        assertThat(new QUser(QUser.user).getType()).isEqualTo(User.class);
        assertThat(new QUser(metadata).getType()).isEqualTo(User.class);
        assertThat(new QUser(metadata, PathInits.DIRECT2).getType()).isEqualTo(User.class);
        assertThat(new QUser(User.class, metadata, PathInits.DIRECT2).getType()).isEqualTo(User.class);
    }

    @Test
    void pathInitsDefaultLeavesDepartmentUninitialized() {
        QUser query = new QUser(QUser.user.getMetadata(), PathInits.DEFAULT);

        assertThat(query.department).isNull();
    }
}
