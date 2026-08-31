package ssu.eatssu.domain.slack.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import static org.assertj.core.api.Assertions.assertThat;

class SlackMessageFormatTest {

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(SlackMessageFormat.class, "serverEnv", "test");
    }

    @Test
    void sendServerErrorFormatsBaseExceptionStatus() {
        String message = SlackMessageFormat.sendServerError(new BaseException(BaseResponseStatus.NOT_FOUND_USER),
                "GET", "/users/me", "1", null);

        assertThat(message).contains("예외 상태코드", "해당 회원을 찾을 수 없습니다.", "개발환경: test");
    }

    @Test
    void sendServerErrorFormatsRegularExceptionAndTruncatesLongArguments() {
        String message = SlackMessageFormat.sendServerError(new IllegalStateException(), "POST", "/reviews", "1",
                "x".repeat(501));

        assertThat(message).contains("예외 타입", "IllegalStateException", "메시지 없음", "...(truncated)");
    }

    @Test
    void slackChannelsExposeConfiguredAddress() {
        assertThat(SlackChannel.SERVER_ERROR.getKrName()).isEqualTo("C092J4J6F0U");
    }
}
