package ssu.eatssu.global.handler;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ssu.eatssu.global.handler.response.BaseResponse;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

/**
 * jwt 인증 실패 시 처리
 * HttpStatus 401
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {

		BaseResponseStatus responseStatus = BaseResponseStatus.UNAUTHORIZED;

		BaseResponse<?> body = BaseResponse.fail(responseStatus);
		String bodyString = objectMapper.writeValueAsString(body);

		//response setting
		response.setStatus(responseStatus.getHttpStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(bodyString);
	}
}
