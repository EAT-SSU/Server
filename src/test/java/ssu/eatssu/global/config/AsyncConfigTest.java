package ssu.eatssu.global.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class AsyncConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(TaskExecutionAutoConfiguration.class))
            .withUserConfiguration(AsyncConfig.class);

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void 비동기_설정이_활성화되어_있다() {
        assertThat(AsyncConfig.class.isAnnotationPresent(Configuration.class)).isTrue();
        assertThat(AsyncConfig.class.isAnnotationPresent(EnableAsync.class)).isTrue();
    }

    @Test
    void propagatesMdcToAsyncThreadAndClearsAfterTask() {
        contextRunner.run(context -> {
            // given
            ThreadPoolTaskExecutor executor = context.getBean(ThreadPoolTaskExecutor.class);
            executor.setCorePoolSize(1);
            MDC.put("requestId", "request-1");

            // when
            Future<String> propagated = executor.submit(() -> MDC.get("requestId"));
            MDC.clear();
            Future<String> cleared = executor.submit(() -> MDC.get("requestId"));

            // then
            assertThat(propagated.get()).isEqualTo("request-1");
            assertThat(cleared.get()).isNull();
        });
    }
}
