package ssu.eatssu.domain.user.entity;

import com.querydsl.core.types.PathMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QBaseTimeEntityTest {

    @Test
    void exposesQuerydslPathsForBaseTimeEntityFields() {
        QBaseTimeEntity query = QBaseTimeEntity.baseTimeEntity;

        assertThat(query.getType()).isEqualTo(BaseTimeEntity.class);
        assertThat(query.createdDate.getMetadata().getName()).isEqualTo("createdDate");
        assertThat(query.modifiedDate.getMetadata().getName()).isEqualTo("modifiedDate");
    }

    @Test
    void everyConstructorOverloadBuildsAnEquivalentPath() {
        PathMetadata metadata = QBaseTimeEntity.baseTimeEntity.getMetadata();

        assertThat(new QBaseTimeEntity("baseTimeEntity").getType()).isEqualTo(BaseTimeEntity.class);
        assertThat(new QBaseTimeEntity(QBaseTimeEntity.baseTimeEntity).getType()).isEqualTo(BaseTimeEntity.class);
        assertThat(new QBaseTimeEntity(metadata).getType()).isEqualTo(BaseTimeEntity.class);
    }
}
