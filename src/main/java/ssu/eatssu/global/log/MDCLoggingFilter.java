package ssu.eatssu.global.log;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCLoggingFilter implements Filter {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final Pattern VALID_REQUEST_ID = Pattern.compile("[A-Za-z0-9-]{1,64}");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestId = httpRequest.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || !VALID_REQUEST_ID.matcher(requestId).matches()) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }
        MDC.put("requestId", requestId);
        MDC.put("uri", httpRequest.getMethod() + " " + httpRequest.getRequestURI());
        ((HttpServletResponse) response).setHeader(REQUEST_ID_HEADER, requestId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
