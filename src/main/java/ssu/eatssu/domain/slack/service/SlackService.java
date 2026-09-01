package ssu.eatssu.domain.slack.service;

import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.slack.entity.SlackChannel;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackService {

    private final MethodsClient methodsClient;

    public void sendSlackMessage(String message, SlackChannel channel) {

        String channelAddress = channel.getKrName();

        try {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                                                                   .channel(channelAddress)
                                                                   .text(message)
                                                                   .build();

            methodsClient.chatPostMessage(request);

            log.info("Slack " + channel + " 에 메시지 보냄");
        } catch (SlackApiException | IOException e) {
            log.error(e.getMessage());
        }
    }
}
