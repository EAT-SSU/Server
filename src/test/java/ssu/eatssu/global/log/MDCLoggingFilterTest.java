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
        request.addHeader("X-Request-ID", "request-1");

        new MDCLoggingFilter().doFilter(request, new MockHttpServletResponse(), (req, res) ->
                assertThat(MDC.get("requestId")).isEqualTo("request-1"));

        assertThat(MDC.get("requestId")).isNull();
    }

    @Test
    void putsMethodAndUriIntoMdcAndClearsAfterFilter() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/v2/reviews");

        // when
        new MDCLoggingFilter().doFilter(request, new MockHttpServletResponse(), (req, res) ->
                assertThat(MDC.get("uri")).isEqualTo("POST /v2/reviews"));

        // then
        assertThat(MDC.get("uri")).isNull();
    }

    @Test
    void 요청_ID가_없으면_새_ID를_생성한다() throws Exception {
        new MDCLoggingFilter().doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), (req, res) ->
                assertThat(MDC.get("requestId")).hasSize(32));
    }

    @Test
    void returnsRequestIdInResponseHeader() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Request-ID", "request-1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        new MDCLoggingFilter().doFilter(request, response, (req, res) -> {});

        // then
        assertThat(response.getHeader("X-Request-ID")).isEqualTo("request-1");
    }

    @Test
    void replacesInvalidRequestIdWithGeneratedOne() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Request-ID", "bad id!" + "a".repeat(64));
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        new MDCLoggingFilter().doFilter(request, response, (req, res) ->
                assertThat(MDC.get("requestId")).matches("[0-9a-f]{32}"));

        // then
        assertThat(response.getHeader("X-Request-ID")).matches("[0-9a-f]{32}");
    }
}
