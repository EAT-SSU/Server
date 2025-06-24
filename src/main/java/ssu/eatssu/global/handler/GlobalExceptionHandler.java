package ssu.eatssu.global.handler;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ssu.eatssu.domain.slack.entity.SlackMessageFormat;
import ssu.eatssu.domain.slack.service.SlackService;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponse;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

/**
 * 전역 Controller 예외처리
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	private final SlackService slackService;
	@Value("${server.env:unknown}")
	private String serverEnv;

	/**
	 * BaseException 처리
	 */
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<BaseResponse<Void>> handleBaseException(BaseException e) {
		log.info(e.getStatus().toString());
		sendErrorToSlack(e);
		return ResponseEntity.status(e.getStatus().getHttpStatus()).body(BaseResponse.fail(e.getStatus()));
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<BaseResponse<Void>> handleAllUnhandledException(Exception ex) {
		sendErrorToSlack(ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR));
	}


	/**
	 * 경로는 있으나 지원하지 않는 http method로 요청 시
	 */
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
		@NonNull HttpRequestMethodNotSupportedException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request) {
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(BaseResponse.fail(
			BaseResponseStatus.METHOD_NOT_ALLOWED));
	}

	/**
	 * 지원하지 않는 content type으로 요청 시
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
		@NonNull HttpMediaTypeNotSupportedException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request) {
		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
							 .body(BaseResponse.fail(BaseResponseStatus.UNSUPPORTED_MEDIA_TYPE));
	}

	/**
	 * 인식할 수 없는 content type으로 요청 시
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
		@NonNull HttpMediaTypeNotAcceptableException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
							 .body(BaseResponse.fail(BaseResponseStatus.NOT_ACCEPTABLE));
	}

	/**
	 * PathVariable 값 누락 시
	 */
	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(
		@NonNull MissingPathVariableException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							 .body(BaseResponse.fail(BaseResponseStatus.MISSING_PATH_VARIABLE));
	}

	/***
	 * RequestParam 값 누락 시
	 */
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
		@NonNull MissingServletRequestParameterException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							 .body(BaseResponse.fail(BaseResponseStatus.MISSING_REQUEST_PARAM));
	}

	/**
	 * RequestPart 값 누락 시
	 */
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(
		@NonNull MissingServletRequestPartException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							 .body(BaseResponse.fail(BaseResponseStatus.MISSING_REQUEST_PART));
	}

	/**
	 * 요청 값 바인딩 처리에 실패한 경우
	 */
	@Override
	protected ResponseEntity<Object> handleServletRequestBindingException(
		@NonNull ServletRequestBindingException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							 .body(BaseResponse.fail(BaseResponseStatus.REQ_BINDING_FAIL));
	}

	/**
	 * request @Valid 유효성 체크를 통과하지 못한 경우
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		@NonNull MethodArgumentNotValidException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							 .body(BaseResponse.fail(BaseResponseStatus.FAILED_VALIDATION));
	}

	/**
	 * Dispatcher Servlet에서 핸들러를 찾지 못한 경우
	 * <p>기본적으로는 <b>404-Not Found</b>응답을 내리지만 Dispatcher Servlet의 throwExceptionIfNoHandlerFound 값이 true인 경우
	 * NoHandlerFoundException 예외를 발생</p>
	 */
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(
		@NonNull NoHandlerFoundException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
							 .body(BaseResponse.fail(BaseResponseStatus.NOT_FOUND));
	}

	/**
	 * 비동기 요청의 응답시간이 초과될 경우
	 */
	@Override
	protected ResponseEntity<Object> handleAsyncRequestTimeoutException(
		@NonNull AsyncRequestTimeoutException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
							 .body(BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_TIME_OUT));
	}

	/**
	 * 파라미터 타입 불일치가 발생한 경우
	 */
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(
		@NonNull TypeMismatchException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							 .body(BaseResponse.fail(BaseResponseStatus.MISMATCH_PARAM_TYPE));
	}

	/**
	 * 적절하지 않은 RequestBody 때문에 HttpMessageConverter.read 메소드 실패한 경우
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
		@NonNull HttpMessageNotReadableException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							 .body(BaseResponse.fail(BaseResponseStatus.BAD_REQUEST));
	}

	/**
	 * 직렬화 실패한 경우
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(
		@NonNull HttpMessageNotWritableException ex,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							 .body(BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR));
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
		@NonNull Exception ex,
		Object body,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode statusCode,
		@NonNull WebRequest request) {

		HttpStatus status = HttpStatus.valueOf(statusCode.value());

		if (status.is4xxClientError() || status.is5xxServerError()) {
			sendErrorToSlack(ex);
		}

		BaseResponseStatus responseStatus = status.is4xxClientError()
			? BaseResponseStatus.BAD_REQUEST
			: BaseResponseStatus.INTERNAL_SERVER_ERROR;

		return ResponseEntity.status(status).body(BaseResponse.fail(responseStatus));
	}

	private void sendErrorToSlack(Exception ex) {
		try {
			String message = SlackMessageFormat.sendServerError(ex);
			slackService.sendSlackMessage(message, ssu.eatssu.domain.slack.entity.SlackChannel.SERVER_ERROR);
		} catch (Exception slackEx) {
			log.warn("슬랙 전송 실패: {}", slackEx.getMessage());
		}
	}


}
