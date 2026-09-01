package ssu.eatssu.global.log;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class MDCLoggingFilterTest {

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void 요청_ID를_전달하고_필터_종료_후_제거한다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-RequestID", "request-1");

        new MDCLoggingFilter().doFilter(request, new MockHttpServletResponse(), (req, res) ->
                assertThat(MDC.get("requestId")).isEqualTo("request-1"));

        assertThat(MDC.get("requestId")).isNull();
    }

    @Test
    void 요청_ID가_없으면_새_ID를_생성한다() throws Exception {
        new MDCLoggingFilter().doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), (req, res) ->
                assertThat(MDC.get("requestId")).hasSize(32));
    }
}
