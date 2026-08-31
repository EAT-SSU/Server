package ssu.eatssu.domain.slack.service;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class SlackErrorNotifierTest {

    @Test
    void 운영환경이_아니면_오류를_슬랙으로_전송하지_않는다() {
        SlackService slackService = mock(SlackService.class);

        new SlackErrorNotifier(slackService).notify(new RuntimeException("error"), "GET", "/health", null, null);

        verifyNoInteractions(slackService);
    }
}
