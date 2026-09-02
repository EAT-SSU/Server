package ssu.eatssu.global.handler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ErrorTestControllerTest {

    @Test
    void 테스트용_런타임_예외를_발생시킨다() {
        assertThatThrownBy(() -> new ErrorTestController().triggerError())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("임의로 발생시킨 런타임 에러입니다!");
    }
}
