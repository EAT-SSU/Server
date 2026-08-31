package ssu.eatssu.global.handler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HealthCheckControllerTest {

    @Test
    void 상태를_UP으로_반환한다() {
        assertThat(new HealthCheckController().health()).containsEntry("status", "UP");
    }
}
