package ssu.eatssu.global.log.event;

import org.junit.jupiter.api.Test;

class LogEventListenerTest {

    @Test
    void 로그_이벤트를_처리한다() {
        new LogEventListener().handleLogEvent(LogEvent.of("event"));
    }
}
