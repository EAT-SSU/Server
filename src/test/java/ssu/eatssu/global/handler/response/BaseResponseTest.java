package ssu.eatssu.global.handler.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseResponseTest {

    @Test
    void 성공과_실패_응답에_상태값을_담는다() {
        BaseResponse<String> success = BaseResponse.success("result");
        BaseResponse<Void> failure = BaseResponse.fail(BaseResponseStatus.NOT_FOUND_USER);

        assertThat(success.getIsSuccess()).isTrue();
        assertThat(success.getCode()).isEqualTo(1000);
        assertThat(success.getResult()).isEqualTo("result");
        assertThat(failure.getIsSuccess()).isFalse();
        assertThat(failure.getCode()).isEqualTo(40401);
    }

    @Test
    void 서버_오류만_슬랙_알림_대상이다() {
        assertThat(BaseResponseStatus.sendSlackNotification(BaseResponseStatus.INTERNAL_SERVER_ERROR)).isTrue();
        assertThat(BaseResponseStatus.sendSlackNotification(BaseResponseStatus.BAD_REQUEST)).isFalse();
    }
}
