package ssu.eatssu.domain.user.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.user.config.UserProperties;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

@Component
@RequiredArgsConstructor
public class NicknameValidator {

    private final UserProperties userProperties;

    public void validateNickname(String nickname){

        // 금지된 닉네임
        if(userProperties.getForbiddenNicknames().stream()
                .anyMatch(forbidden -> forbidden.equalsIgnoreCase(nickname))){
            throw new BaseException(BaseResponseStatus.FORBIDDEN_NICKNAME);
        }

        // 부적절한 길이
        if(nickname.length() <= 0 || nickname.length() > 16){
            throw new BaseException(BaseResponseStatus.INVALID_NICKNAME_LENGTH);
        }

        // 숫자로만 구성된 닉네임
        if (nickname.matches("^[0-9]+$")) {
            throw new BaseException(BaseResponseStatus.NUMBER_ONLY_NICKNAME);
        }

        // 연속된 공백
        if (nickname.matches(".*\\s{2,}.*")){
            throw new BaseException(BaseResponseStatus.CONSECUTIVE_SPACES_NICKNAME);
        }

        // 연속된 하이폰(-)
        if (nickname.matches(".*-{2,}.*")){
            throw new BaseException(BaseResponseStatus.CONSECUTIVE_HYPHEN_NICKNAME);
        }

        // 한글 영어가 아닌 것으로 시작
        if (nickname.matches("^[^A-Za-z가-힣].*")) {
            throw new BaseException(BaseResponseStatus.INVALID_START_OF_NICKNAME);
        }

        // 한글 영어가 아닌 것으로 마무리
        if (nickname.matches(".*[^A-Za-z가-힣]$")) {
            throw new BaseException(BaseResponseStatus.INVALID_END_OF_NICKNAME);
        }

        // 한글, 숫자, 영어, 하이폰(-), 공백을 제외한 것이 포함됨.
        if (!nickname.matches("^[A-Za-z0-9가-힣- ]+$")){
            throw new BaseException(BaseResponseStatus.INVALID_SYMBOL_NICKNAME);
        }
    }
}
