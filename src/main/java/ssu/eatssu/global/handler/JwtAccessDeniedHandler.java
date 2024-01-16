package ssu.eatssu.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import ssu.eatssu.global.handler.response.BaseResponse;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.io.IOException;

/**
 * jwt 인가 실패 시 처리
 * HttpStatus 403
 */
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        BaseResponseStatus responseStatus = BaseResponseStatus.FORBIDDEN;

        BaseResponse<?> body = BaseResponse.fail(responseStatus);
        String bodyString = objectMapper.writeValueAsString(body);

        //response setting
        response.setStatus(responseStatus.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(bodyString);
    }
}
