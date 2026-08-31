package ssu.eatssu;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static org.assertj.core.api.Assertions.assertThat;

class EatSsuApplicationMetadataTest {

    @Test
    void applicationIsConfiguredForSpringBootAndJpaAuditing() {
        assertThat(EatSsuApplication.class).hasAnnotation(SpringBootApplication.class);
        assertThat(EatSsuApplication.class).hasAnnotation(EnableJpaAuditing.class);
    }
}
