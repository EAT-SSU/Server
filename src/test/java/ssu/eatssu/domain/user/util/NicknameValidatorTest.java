package ssu.eatssu.domain.user.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NicknameValidatorTest {

    private NicknameValidator nicknameValidator = new NicknameValidator();

    @ParameterizedTest
    @ValueSource(strings = {"ㅅㅂ","뻐킹","미친사람"})
    void 비속어_포함_시_예외가_발생한다(String input) {
        // when & then
        assertThatThrownBy(()->nicknameValidator.validateNickname(input))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.PROFANITY_NICKNAME);
    }


    @ParameterizedTest
    @ValueSource(strings = {"잇슈","EAT-SSU","eatssu"})
    void 단독으로_브랜드명_사용_시_예외가_발생한다(String input) {
        // when & then
        assertThatThrownBy(()->nicknameValidator.validateNickname(input))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.SERVICE_BRAND_NICKNAME);
    }

    @ParameterizedTest
    @ValueSource(strings = {"이슈 운영자","서버 관리자","admin입니다"})
    void 관리자로_혼동될_수_있는_단어를_포함하면_예외가_발생한다(String input) {
        // when & then
        assertThatThrownBy(()->nicknameValidator.validateNickname(input))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.ADMIN_MANAGER_NICKNAME);
    }

    @ParameterizedTest
    @ValueSource(strings = {"유효한 닉네임","available nic","가능한-닉네임","유-효-한-닉-네-임","유 효 한 닉 1 2 a","ㅇㅅㅎㅇㅌ","0이름","0test","이름0","test0"})
    void doesNotThrowWhenNicknameIsValid(String input) {
        // given: parameterized valid nickname input

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
    @ValueSource(strings = {"-test"," test"})
    void throwsExceptionWhenNicknameStartsWithInvalidCharacter(String input) {
        // given: parameterized nickname starting with a symbol (숫자는 시작 문자로 허용됨)

        // when & then
        assertThatThrownBy(()->nicknameValidator.validateNickname(input))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.INVALID_START_OF_NICKNAME);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test-","test "})
    void throwsExceptionWhenNicknameEndsWithInvalidCharacter(String input) {
        // given: parameterized nickname ending with a symbol (숫자는 끝 문자로 허용됨)

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
