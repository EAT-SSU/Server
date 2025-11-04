package ssu.eatssu.domain.slack.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.slack.entity.SlackChannel;
import ssu.eatssu.domain.slack.entity.SlackMessageFormat;
import ssu.eatssu.global.handler.response.BaseException;

@Service
@Slf4j
public class SlackErrorNotifier {
    private final SlackService slackService;
    @Value("${server.env:unknown}")
    private String serverEnv;

    public SlackErrorNotifier(SlackService slackService) {
        this.slackService = slackService;
    }

    public void notify(Throwable ex, String method, String uri, String userId, String args) {
        if (!"prod".equals(serverEnv)) {
            return;
        }
        try {
            String message = SlackMessageFormat.sendServerError(ex, method, uri, userId, args);
            slackService.sendSlackMessage(message, SlackChannel.SERVER_ERROR);
        } catch (Exception slackEx) {
            log.warn("슬랙 전송 실패: {}", slackEx.getMessage());
        }
    }
}

