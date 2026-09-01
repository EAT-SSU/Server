package ssu.eatssu.global.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import static org.assertj.core.api.Assertions.assertThat;

class AsyncConfigTest {

    @Test
    void 비동기_설정이_활성화되어_있다() {
        assertThat(AsyncConfig.class.isAnnotationPresent(Configuration.class)).isTrue();
        assertThat(AsyncConfig.class.isAnnotationPresent(EnableAsync.class)).isTrue();
    }
}
