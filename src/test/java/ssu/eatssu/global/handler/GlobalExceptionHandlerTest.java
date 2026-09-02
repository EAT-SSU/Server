package ssu.eatssu.global.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ssu.eatssu.domain.slack.service.SlackErrorNotifier;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponse;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(mock(SlackErrorNotifier.class));
    private final HttpHeaders headers = new HttpHeaders();
    private final org.springframework.web.context.request.WebRequest request = mock(org.springframework.web.context.request.WebRequest.class);

    @Test
    void 업무_예외와_미처리_예외를_상태에_맞게_변환한다() {
        assertStatus(handler.handleBaseException(new BaseException(BaseResponseStatus.NOT_FOUND_MENU)), HttpStatus.NOT_FOUND);
        assertStatus(handler.handleAllUnhandledException(new RuntimeException()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void HTTP_메서드와_미디어_타입_예외를_변환한다() {
        assertStatus(handler.handleHttpRequestMethodNotSupported(new HttpRequestMethodNotSupportedException("POST"), headers,
                                                                  HttpStatus.METHOD_NOT_ALLOWED, request), HttpStatus.METHOD_NOT_ALLOWED);
        assertStatus(handler.handleHttpMediaTypeNotSupported(new HttpMediaTypeNotSupportedException(MediaType.TEXT_PLAIN.toString()), headers,
                                                              HttpStatus.UNSUPPORTED_MEDIA_TYPE, request), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertStatus(handler.handleHttpMediaTypeNotAcceptable(new HttpMediaTypeNotAcceptableException(List.of(MediaType.APPLICATION_JSON)), headers,
                                                              HttpStatus.NOT_ACCEPTABLE, request), HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    void 요청_누락과_검증_예외를_변환한다() throws Exception {
        MethodParameter parameter = mock(MethodParameter.class);
        assertStatus(handler.handleMissingPathVariable(new MissingPathVariableException("id", parameter), headers,
                                                        HttpStatus.BAD_REQUEST, request), HttpStatus.BAD_REQUEST);
        assertStatus(handler.handleMissingServletRequestParameter(new MissingServletRequestParameterException("id", "Long"), headers,
                                                                  HttpStatus.BAD_REQUEST, request), HttpStatus.BAD_REQUEST);
        assertStatus(handler.handleMissingServletRequestPart(new MissingServletRequestPartException("file"), headers,
                                                             HttpStatus.BAD_REQUEST, request), HttpStatus.BAD_REQUEST);
        assertStatus(handler.handleServletRequestBindingException(new ServletRequestBindingException("invalid"), headers,
                                                                  HttpStatus.BAD_REQUEST, request), HttpStatus.BAD_REQUEST);
        assertStatus(handler.handleMethodArgumentNotValid(new MethodArgumentNotValidException(parameter,
                                                                                               new BeanPropertyBindingResult(new Object(), "request")),
                                                          headers, HttpStatus.BAD_REQUEST, request), HttpStatus.BAD_REQUEST);
    }

    @Test
    void 요청_처리_예외를_변환한다() {
        assertStatus(handler.handleNoHandlerFoundException(new NoHandlerFoundException("GET", "/missing", headers), headers,
                                                           HttpStatus.NOT_FOUND, request), HttpStatus.NOT_FOUND);
        assertStatus(handler.handleAsyncRequestTimeoutException(new AsyncRequestTimeoutException(), headers,
                                                                HttpStatus.SERVICE_UNAVAILABLE, request), HttpStatus.SERVICE_UNAVAILABLE);
        assertStatus(handler.handleTypeMismatch(new TypeMismatchException("x", Long.class), headers,
                                                HttpStatus.BAD_REQUEST, request), HttpStatus.BAD_REQUEST);
        assertStatus(handler.handleHttpMessageNotReadable(new HttpMessageNotReadableException("bad", new MockHttpInputMessage(new byte[0])), headers,
                                                          HttpStatus.BAD_REQUEST, request), HttpStatus.BAD_REQUEST);
        assertStatus(handler.handleHttpMessageNotWritable(new HttpMessageNotWritableException("bad"), headers,
                                                          HttpStatus.INTERNAL_SERVER_ERROR, request), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void 내부_예외는_4xx와_5xx로_구분한다() {
        assertStatus(handler.handleExceptionInternal(new RuntimeException(), null, headers, HttpStatus.BAD_REQUEST, request),
                     HttpStatus.BAD_REQUEST);
        assertStatus(handler.handleExceptionInternal(new RuntimeException(), null, headers, HttpStatus.INTERNAL_SERVER_ERROR, request),
                     HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void assertStatus(org.springframework.http.ResponseEntity<?> response, HttpStatus status) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(((BaseResponse<?>) response.getBody()).getIsSuccess()).isFalse();
    }
}
