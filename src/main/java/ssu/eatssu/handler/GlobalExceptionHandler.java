package ssu.eatssu.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
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
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.handler.response.BaseResponse;
import ssu.eatssu.handler.response.BaseResponseStatus;

/**
 * 전역 Controller 예외처리
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * BaseException 처리
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse> handleBaseException(BaseException e) {
        log.info(e.getStatus().toString());
        return ResponseEntity.status(e.getStatus().getHttpStatus()).body(BaseResponse.fail(e.getStatus()));
    }

    /**
     * 경로는 있으나 지원하지 않는 http method로 요청 시
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(BaseResponse.fail(BaseResponseStatus.METHOD_NOT_ALLOWED));
    }

    /**
     * 지원하지 않는 content type으로 요청 시
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(BaseResponse.fail(BaseResponseStatus.UNSUPPORTED_MEDIA_TYPE));

    }

    /**
     * 인식할 수 없는 content type으로 요청 시
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
                                                                      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(BaseResponse.fail(BaseResponseStatus.NOT_ACCEPTABLE));
    }

    /**
     * PathVariable 값 누락 시
     */
    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers,
                                                               HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponse.fail(BaseResponseStatus.MISSING_PATH_VARIABLE));
    }

    /***
     * RequestParam 값 누락 시
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponse.fail(BaseResponseStatus.MISSING_REQUEST_PARAM));
    }

    /**
     * RequestPart 값 누락 시
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponse.fail(BaseResponseStatus.MISSING_REQUEST_PART));
    }

    /**
     * 요청 값 바인딩 처리에 실패한 경우
     */
    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
                                                                          HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponse.fail(BaseResponseStatus.REQ_BINDING_FAIL));
    }

    /**
     *	request @Valid 유효성 체크를 통과하지 못한 경우
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponse.fail(BaseResponseStatus.FAILED_VALIDATION));
    }

    /**
     * Dispatcher Servlet에서 핸들러를 찾지 못한 경우
     * <p>기본적으로는 <b>404-Not Found</b>응답을 내리지만 Dispatcher Servlet의 throwExceptionIfNoHandlerFound 값이 true인 경우
     * NoHandlerFoundException 예외를 발생</p>
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BaseResponse.fail(BaseResponseStatus.NOT_FOUND));
    }

    /**
     * 비동기 요청의 응답시간이 초과될 경우
     */
    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_TIME_OUT));
    }

    /**
     * 파라미터 타입 불일치가 발생한 경우
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponse.fail(BaseResponseStatus.MISMATCH_PARAM_TYPE));
    }

    /**
     * 적절하지 않은 RequestBody 때문에 HttpMessageConverter.read 메소드 실패한 경우
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponse.fail(BaseResponseStatus.BAD_REQUEST));
    }

    /**
     * 직렬화 실패한 경우
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR));
    }

}
