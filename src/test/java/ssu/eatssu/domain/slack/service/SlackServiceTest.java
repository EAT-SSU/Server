package ssu.eatssu.domain.slack.service;

import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.slack.entity.SlackChannel;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SlackServiceTest {

    @Test
    void sendSlackMessagePostsToConfiguredChannel() throws Exception {
        MethodsClient methodsClient = mock(MethodsClient.class);
        SlackService slackService = new SlackService(methodsClient);

        slackService.sendSlackMessage("에러 발생", SlackChannel.SERVER_ERROR);

        verify(methodsClient).chatPostMessage(argThatMatchesChannelAndText(SlackChannel.SERVER_ERROR.getKrName(), "에러 발생"));
    }

    @Test
    void sendSlackMessageSwallowsSlackApiException() throws Exception {
        MethodsClient methodsClient = mock(MethodsClient.class);
        when(methodsClient.chatPostMessage(any(ChatPostMessageRequest.class)))
                .thenThrow(mock(SlackApiException.class));
        SlackService slackService = new SlackService(methodsClient);

        assertThatCode(() -> slackService.sendSlackMessage("에러 발생", SlackChannel.SERVER_ERROR))
                .doesNotThrowAnyException();
    }

    @Test
    void sendSlackMessageSwallowsIOException() throws Exception {
        MethodsClient methodsClient = mock(MethodsClient.class);
        when(methodsClient.chatPostMessage(any(ChatPostMessageRequest.class)))
                .thenThrow(new IOException("연결 실패"));
        SlackService slackService = new SlackService(methodsClient);

        assertThatCode(() -> slackService.sendSlackMessage("에러 발생", SlackChannel.SERVER_ERROR))
                .doesNotThrowAnyException();
    }

    private ChatPostMessageRequest argThatMatchesChannelAndText(String channel, String text) {
        return org.mockito.ArgumentMatchers.argThat(request ->
                request.getChannel().equals(channel) && request.getText().equals(text));
    }
}
