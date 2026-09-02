package ssu.eatssu.global.log.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogEventTest {

    @Test
    void 메시지로_이벤트를_생성한다() {
        assertThat(LogEvent.of("created").message()).isEqualTo("created");
    }
}
