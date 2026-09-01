package ssu.eatssu.domain.user.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserPropertiesTest {

    @Test
    void storesForbiddenNicknames() {
        UserProperties properties = new UserProperties();

        assertThat(properties.getForbiddenNicknames()).isEmpty();
        properties.setForbiddenNicknames(List.of("관리자", "운영자"));

        assertThat(properties.getForbiddenNicknames()).containsExactly("관리자", "운영자");
    }
}
