package ssu.eatssu.response;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {

    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    VALIDATION_ERROR(false, 2000, "입력값을 확인해주세요."),
    NOT_FOUND_USER(false, 2001, "해당 유저를 찾을 수 없습니다."),
    NOT_FOUND_MENU(false, 2002, "해당 메뉴를 찾을 수 없습니다."),
    NOT_FOUND_MEAL(false, 2003, "해당 식단을 찾을 수 없습니다."),
    NOT_FOUND_RESTAURANT(false, 2004, "해당 식당을 찾을 수 없습니다."),
    NOT_FOUND_REVIEW(false, 2005, "해당 리뷰를 찾을 수 없습니다."),
    NOT_SUPPORT_RESTAURANT(false, 2006, "해당 식당은 지원하지 않습니다."),
    PERMISSION_DENIED(false, 2007, "권한이 없습니다."),

    MISSING_QUERY_PARAM(false, 2008, "쿼리 파라미터가 누락되었습니다."),
    INVALID_TOKEN(false, 2009, "유효하지 않은 토큰 값입니다." ),
    FAIL_MAKE_TOKEN(false, 2010, "토큰 생성에 실패했습니다."),
    NICKNAME_DUPLICATE(false, 2011, "이미 존재하는 닉네임입니다."),
    EMAIL_DUPLICATE(false, 2012, "이미 존재하는 이메일입니다."),


    DUPLICATE_EMAIL(false, 2011, "중복된 이메일입니다."),
    DUPLICATE_NICKNAME(false, 2012, "중복된 닉네임입니다."),

    INVALID_DATE(false, 2013, "잘못된 날짜형식입니다."),


    /**
     * 3000 : Response 오류
     */
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    FAIL_IMG_UPLOAD(false, 4001, "이미지 업로드에 실패했습니다.")
    ;

    private final boolean isSuccess;
    private final int code;
    private final String message;

    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
