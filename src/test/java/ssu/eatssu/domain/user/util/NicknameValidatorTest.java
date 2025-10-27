package ssu.eatssu.domain.user.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ssu.eatssu.domain.user.config.UserProperties;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NicknameValidatorTest {

    private NicknameValidator nicknameValidator = new NicknameValidator(new UserProperties());

    @ParameterizedTest
    @ValueSource(strings = {"유효한 닉네임","available nic","가능한-닉네임","유-효-한-닉-네-임","유 효 한 닉 1 2 a"})
    void 유효한_이름을_입력하면_예외가_발생하지_않는다(String input) {
        // when & then
        assertDoesNotThrow(()-> nicknameValidator.validateNickname(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"","testtesttesttesttesttesttest"})
    void 길이가_1자_이상_16자_이하가_아닌_경우_예외가_발생한다(String input) {
        // when & then
       assertThatThrownBy(()->nicknameValidator.validateNickname(input))
               .isInstanceOf(BaseException.class)
               .extracting("status")
               .isEqualTo(BaseResponseStatus.INVALID_NICKNAME_LENGTH);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12","1234","532532","4444"})
    void 숫자로만_구성된_닉네임인_경우_예외가_발생한다(String input) {
        // when & then
        assertThatThrownBy(()->nicknameValidator.validateNickname(input))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.NUMBER_ONLY_NICKNAME);
    }

    @ParameterizedTest
    @ValueSource(strings = {"이름  이름","q  q","te  st","t  t  t"})
    void 연속된_공백으로_구성된_닉네임인_경우_예외가_발생한다(String input) {
        // when & then
        assertThatThrownBy(()->nicknameValidator.validateNickname(input))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.CONSECUTIVE_SPACES_NICKNAME);
    }

    @ParameterizedTest
    @ValueSource(strings = {"이름--이름","q--q","te--st","t--t--t"})
    void 연속된_하이폰으로_구성된_닉네임인_경우_예외가_발생한다(String input) {
        // when & then
        assertThatThrownBy(()->nicknameValidator.validateNickname(input))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.CONSECUTIVE_HYPHEN_NICKNAME);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0이름","0test","-test"," test"})
    void 한글_또는_영어로_시작하지_않는_닉네임인_경우_예외가_발생한다(String input) {
        // when & then
        assertThatThrownBy(()->nicknameValidator.validateNickname(input))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.INVALID_START_OF_NICKNAME);
    }

    @ParameterizedTest
    @ValueSource(strings = {"이름0","test0","test-","test "})
    void 한글_또는_영어로_끝나지_않는_닉네임인_경우_예외가_발생한다(String input) {
        // when & then
        assertThatThrownBy(()->nicknameValidator.validateNickname(input))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.INVALID_END_OF_NICKNAME);
    }

    @ParameterizedTest
    @ValueSource(strings = {"이름$이름","특수**^munza","포함^^입니다","test~~nick"})
    void 한글_영어_숫자_공백_하이폰을_제외한_특수문자를_포함하면_예외가_발생한다(String input) {
        // when & then
        assertThatThrownBy(()->nicknameValidator.validateNickname(input))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.INVALID_SYMBOL_NICKNAME);
    }
}
