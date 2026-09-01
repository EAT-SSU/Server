package ssu.eatssu.global.config;

import com.slack.api.methods.MethodsClient;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class SlackConfigTest {

    @Test
    void 슬랙_메서드_클라이언트를_생성한다() {
        SlackConfig config = new SlackConfig();
        ReflectionTestUtils.setField(config, "slackToken", "xoxb-test-token");

        MethodsClient client = config.slackMethodsClient();

        assertThat(client).isNotNull();
    }
}
