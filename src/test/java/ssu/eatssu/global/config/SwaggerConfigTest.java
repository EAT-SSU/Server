package ssu.eatssu.global.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class SwaggerConfigTest {

    @Test
    void JWT_보안_스키마와_서버_정보를_생성한다() {
        SwaggerConfig config = new SwaggerConfig();
        ReflectionTestUtils.setField(config, "SERVER_URL", "https://api.example.com");
        ReflectionTestUtils.setField(config, "SERVER_DESCRIPTION", "test");

        var openApi = config.openAPI();

        assertThat(openApi.getServers()).extracting("url").containsExactly("https://api.example.com");
        assertThat(openApi.getComponents().getSecuritySchemes()).containsKey("bearerAuth");
    }
}
