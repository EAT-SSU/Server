package ssu.eatssu.global.config;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class InfrastructureConfigTest {

    @Test
    void 비동기_설정을_생성한다() {
        assertThat(new AsyncConfig()).isNotNull();
    }

    @Test
    void QueryDsl_팩토리를_생성한다() {
        QueryDslConfig config = new QueryDslConfig();
        ReflectionTestUtils.setField(config, "entityManager", mock(EntityManager.class));

        assertThat(config.queryFactory()).isNotNull();
    }

    @Test
    void S3_클라이언트를_생성한다() {
        S3Config config = new S3Config();
        ReflectionTestUtils.setField(config, "accessKey", "access-key");
        ReflectionTestUtils.setField(config, "secretKey", "secret-key");
        ReflectionTestUtils.setField(config, "region", "ap-northeast-2");

        assertThat(config.amazonS3Client()).isNotNull();
    }
}
