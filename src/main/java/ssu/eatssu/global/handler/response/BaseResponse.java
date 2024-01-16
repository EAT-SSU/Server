package ssu.eatssu.global.handler.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {

    private final Boolean isSuccess;
    private final String message;
    private final int code;

    @JsonInclude(JsonInclude.Include.NON_NULL) //Json으로 응답이 나갈 때 null인 경우 포함되지 않음
    private T result;

    /**
     * 정적 팩토리 메서드
     */
    private BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
    }

    private BaseResponse(BaseResponseStatus status, T result) {
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
        this.result = result;
    }


    /**
     * API 성공 응답
     */
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    public static <T> BaseResponse<T> success(T result) {
        return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
    }

    /**
     * API 실패 응답
     */
    public static <T> BaseResponse<T> fail(BaseResponseStatus status) {
        return new BaseResponse<>(status);
    }
}
