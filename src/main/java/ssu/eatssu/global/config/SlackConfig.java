package ssu.eatssu.global.config;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfig {

    @Value("${slack.token}")
    private String slackToken;

    @Bean
    public MethodsClient slackMethodsClient() {
        return Slack.getInstance().methods(slackToken);
    }
}
