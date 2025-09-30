package ssu.eatssu.global.handler.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {

    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, HttpStatus.OK, 1000, "요청에 성공하였습니다."),

    /**
     * 400 BAD_REQUEST 잘못된 요청
     */
    BAD_REQUEST(false, HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),
    VALIDATION_ERROR(false, HttpStatus.BAD_REQUEST, 40001, "입력값을 확인해주세요."),
    MISSING_PATH_VARIABLE(false, HttpStatus.BAD_REQUEST, 40002, "경로 변수가 누락되었습니다."),
    MISSING_REQUEST_PARAM(false, HttpStatus.BAD_REQUEST, 40003, "쿼리 파라미터가 누락되었습니다."),
    MISSING_REQUEST_PART(false, HttpStatus.BAD_REQUEST, 40004, "multipart/form-data 파일이 누락되었습니다."),
    REQ_BINDING_FAIL(false, HttpStatus.BAD_REQUEST, 40005, "잘못된 request 입니다."),
    MISMATCH_PARAM_TYPE(false, HttpStatus.BAD_REQUEST, 40006, "잘못된 파라미터 타입입니다."),
    FAILED_VALIDATION(false, HttpStatus.BAD_REQUEST, 40007, "입력값이 누락되었거나, 부적절한 입력 값이 있습니다."),
    INVALID_DATE(false, HttpStatus.BAD_REQUEST, 40008, "잘못된 날짜형식입니다."),
    NOT_SUPPORT_RESTAURANT(false, HttpStatus.BAD_REQUEST, 40009, "해당 식당은 지원하지 않습니다."),
    INVALID_IDENTITY_TOKEN(false, HttpStatus.BAD_REQUEST, 40010, "잘못된 identityToken 입니다."),
    EXISTED_MEAL(false, HttpStatus.BAD_REQUEST, 40011, "이미 존재하는 식단입니다."),
    INVALID_TARGET_TYPE(false, HttpStatus.BAD_REQUEST, 40012, "잘못된 targetType 입니다."),
    MISSING_USER_DEPARTMENT(false, HttpStatus.BAD_REQUEST, 40013, "사용자의 학과 정보가 없습니다."),
    RECENT_REPORT_ON_REVIEW(false, HttpStatus.BAD_REQUEST, 40014, "24시간 이내에 동일 댓글을 신고했습니다."),

    /**
     * 401 UNAUTHORIZED 권한없음(인증 실패)
     */
    UNAUTHORIZED(false, HttpStatus.UNAUTHORIZED, 401, "인증에 실패했습니다."),
    INVALID_TOKEN(false, HttpStatus.UNAUTHORIZED, 401, "유효하지 않은 토큰 입니다."),

    /**
     * 403 FORBIDDEN 권한없음(인가 실패)
     */
    FORBIDDEN(false, HttpStatus.FORBIDDEN, 403, "권한이 없습니다."),
    REVIEW_PERMISSION_DENIED(false, HttpStatus.FORBIDDEN, 40301, "리뷰에 대한 권한이 없습니다."),

    /**
     * 404 NOT_FOUND 잘못된 리소스 접근
     */
    NOT_FOUND(false, HttpStatus.NOT_FOUND, 404, "요청한 리소스를 찾을 수 없습니다."),
    NOT_FOUND_USER(false, HttpStatus.NOT_FOUND, 40401, "해당 회원을 찾을 수 없습니다."),
    NOT_FOUND_MENU(false, HttpStatus.NOT_FOUND, 40402, "해당 메뉴를 찾을 수 없습니다."),
    NOT_FOUND_MEAL(false, HttpStatus.NOT_FOUND, 40403, "해당 식단을 찾을 수 없습니다."),
    NOT_FOUND_RESTAURANT(false, HttpStatus.NOT_FOUND, 40404, "해당 식당을 찾을 수 없습니다."),
    NOT_FOUND_REVIEW(false, HttpStatus.NOT_FOUND, 40405, "해당 리뷰을 찾을 수 없습니다."),
    NOT_FOUND_REVIEW_REPORT(false, HttpStatus.NOT_FOUND, 40406, "해당 리뷰 신고 내역을 찾을 수 없습니다."),
    NOT_FOUND_USER_INQUIRY(false, HttpStatus.NOT_FOUND, 40407, "해당 문의 내역을 찾을 수 없습니다."),
    NOT_FOUND_COLLEGE(false, HttpStatus.NOT_FOUND, 40408, "해당 대학을 찾을 수 없습니다."),
    NOT_FOUND_DEPARTMENT(false, HttpStatus.NOT_FOUND, 40409, "해당 학과를 찾을 수 없습니다."),
    NOT_FOUND_PARTNERSHIP(false, HttpStatus.NOT_FOUND, 40410, "해당 제휴를 찾을 수 없습니다."),
    NOT_FOUND_PARTNERSHIP_RESTAURANT(false, HttpStatus.NOT_FOUND, 40411, "해당 제휴 식당을 찾을 수 없습니다."),
    INVALID_NICKNAME(false, HttpStatus.NOT_FOUND, 40412, "잘못된 닉네임입니다."),

    /**
     * 405 METHOD_NOT_ALLOWED 지원하지 않은 method 호출
     */
    METHOD_NOT_ALLOWED(false, HttpStatus.METHOD_NOT_ALLOWED, 405, "해당 method는 지원하지 않습니다."),

    /**
     * 406 NOT_ACCEPTABLE 인식할 수 없는 content type
     */
    NOT_ACCEPTABLE(false, HttpStatus.NOT_ACCEPTABLE, 406, "인식할 수 없는 미디어 타입입니다."),

    /**
     * 409 CONFLICT 중복된 리소스
     */
    CONFLICT(false, HttpStatus.CONFLICT, 409, "중복된 리소스를 입력했습니다."),
    DUPLICATE_EMAIL(false, HttpStatus.CONFLICT, 40901, "중복된 이메일입니다."),
    DUPLICATE_NICKNAME(false, HttpStatus.CONFLICT, 40902, "중복된 닉네임입니다."),

    /**
     * 415 UNSUPPORTED_MEDIA_TYPE 지원하지 않는 content type
     */
    UNSUPPORTED_MEDIA_TYPE(false, HttpStatus.UNSUPPORTED_MEDIA_TYPE, 415, "지원하지 않는 미디어 타입입니다."),

    /**
     * 500 INTERNAL_SERVER_ERROR 서버 내부 에러
     */
    INTERNAL_SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 내부 에러입니다"),
    FAIL_IMAGE_UPLOAD(false, HttpStatus.INTERNAL_SERVER_ERROR, 50001, "이미지 업로드에 실패했습니다."),

    /**
     * 503 SERVICE_UNAVAILABLE 서버 내부 에러
     */
    SERVICE_UNAVAILABLE(false, HttpStatus.SERVICE_UNAVAILABLE, 503, "현재 서비스가 불가능한 상태입니다."),
    INTERNAL_SERVER_TIME_OUT(false, HttpStatus.SERVICE_UNAVAILABLE, 503, "서버에서 시간초과가 발생했습니다.");

    private final boolean isSuccess;
    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    BaseResponseStatus(boolean isSuccess, HttpStatus httpStatus, int code, String message) {
        this.isSuccess = isSuccess;
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public static boolean sendSlackNotification(BaseResponseStatus baseResponseStatus) {
        return baseResponseStatus.httpStatus.is5xxServerError();
    }
}
