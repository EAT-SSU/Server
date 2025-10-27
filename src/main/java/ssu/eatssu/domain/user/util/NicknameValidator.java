package ssu.eatssu.domain.user.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.user.config.UserProperties;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NicknameValidator {

    private static final Set<String> SERVICE_NAME_BRAND = Set.of(
            "EAT-SSU", "EATSSU", "잇슈", "읻슈", "잍슈", "잇쓔", "잇쓲", "잇씨유", "잇슈우", "잇슈웅",
            "eat-ssu", "eatssu", "eatsu", "e4tssu", "3at-ssu", "ea7-ssu", "e @t-ssu", "e.at.ssu", "e-a-t-s-s-u", "e_a_t_s_s_u",
            "e a t ssu", "eat_ssu", "eatssu_", "eatssu123", "e @tssu", "ēat-ssu", "3atssu", "eat$u", "eat5su", "eats$u",
            "eats-u", "E4T슈"
    );

    private static final Set<String> ADMIN_LIKE_WORDS = Set.of(
            "admin", "manager", "운영자", "관리자", "system"
    );

    public void validateNickname(String nickname){

        // 서비스 브랜드명 포함 닉네임
        if(SERVICE_NAME_BRAND.contains(nickname)){
            throw new BaseException(BaseResponseStatus.SERVICE_BRAND_NICKNAME);
        }

        // 운영자, 관리자 혼동 닉네임
        if (ADMIN_LIKE_WORDS.stream()
                .anyMatch(word -> nickname.toLowerCase().contains(word.toLowerCase()))) {
            throw new BaseException(BaseResponseStatus.ADMIN_MANGER_NICKNAME);
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
