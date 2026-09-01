package ssu.eatssu.domain.slack.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.slack.entity.SlackChannel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class SlackErrorNotifierTest {

    @Test
    void 운영환경이_아니면_오류를_슬랙으로_전송하지_않는다() {
        SlackService slackService = mock(SlackService.class);

        new SlackErrorNotifier(slackService).notify(new RuntimeException("error"), "GET", "/health", null, null);

        verifyNoInteractions(slackService);
    }

    @Test
    void 운영환경이면_오류를_슬랙으로_전송한다() {
        SlackService slackService = mock(SlackService.class);
        SlackErrorNotifier notifier = new SlackErrorNotifier(slackService);
        ReflectionTestUtils.setField(notifier, "serverEnv", "prod");

        notifier.notify(new RuntimeException("error"), "GET", "/health", "1", "[]");

        verify(slackService).sendSlackMessage(any(String.class), eq(SlackChannel.SERVER_ERROR));
    }

    @Test
    void 슬랙_전송이_실패해도_예외를_전파하지_않는다() {
        SlackService slackService = mock(SlackService.class);
        doThrow(new RuntimeException("slack down")).when(slackService)
                                                    .sendSlackMessage(any(String.class), eq(SlackChannel.SERVER_ERROR));
        SlackErrorNotifier notifier = new SlackErrorNotifier(slackService);
        ReflectionTestUtils.setField(notifier, "serverEnv", "prod");

        notifier.notify(new RuntimeException("error"), "GET", "/health", "1", "[]");

        verify(slackService).sendSlackMessage(any(String.class), eq(SlackChannel.SERVER_ERROR));
    }
}
