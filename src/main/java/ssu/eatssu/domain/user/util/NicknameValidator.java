package ssu.eatssu.domain.user.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.Set;
import java.util.regex.Pattern;

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

    private static final String PROFANITY_REGEX =
            "[시씨씪슈쓔쉬쉽쒸쓉](?:[0-9]*|[0-9]+ *)[바발벌빠빡빨뻘파팔펄]|" +
                    "[섊좆좇졷좄좃좉졽썅춍봊]|" +
                    "[ㅈ조][0-9]*까|ㅅㅣㅂㅏㄹ?|ㅂ[0-9]*ㅅ|" +
                    "[ㅄᄲᇪᄺᄡᄣᄦᇠ]|" +
                    "[ㅅㅆᄴ][0-9]*[ㄲㅅㅆᄴㅂ]|" +
                    "[존좉좇][0-9 ]*나|" +
                    "[자보][0-9]+지|보빨|" +
                    "[봊봋봇봈볻봁봍] *[빨이]|" +
                    "[후훚훐훛훋훗훘훟훝훑][장앙]|" +
                    "[엠앰]창|애[미비]|애자|" +
                    "[가-탏탑-힣]색기|" +
                    "(?:[샊샛세쉐쉑쉨쉒객갞갟갯갰갴겍겎겏겤곅곆곇곗곘곜걕걖걗걧걨걬] *[끼키퀴])|" +
                    "새 *[키퀴]|" +
                    "[병븅][0-9]*[신딱딲]|" +
                    "미친[가-닣닥-힣]|[믿밑]힌|[염옘][0-9]*병|" +
                    "[샊샛샜샠섹섺셋셌셐셱솃솄솈섁섂섓섔섘]기|" +
                    "[섹섺섻쎅쎆쎇쎽쎾쎿섁섂섃썍썎썏][스쓰]|" +
                    "[지야][0-9]*랄|니[애에]미|" +
                    "갈[0-9]*보[^가-힣]|" +
                    "[뻐뻑뻒뻙뻨][0-9]*[뀨큐킹낑)]|" +
                    "꼬[0-9]*추|곧[0-9]*휴|" +
                    "[가-힣]슬아치|자[0-9]*박꼼|빨통|" +
                    "[사싸](?:이코|가지|[0-9]*까시)|" +
                    "육[0-9]*시[랄럴]|육[0-9]*실[알얼할헐]|" +
                    "즐[^가-힣]|찌[0-9]*(?:질이|랭이)|" +
                    "찐[0-9]*따|찐[0-9]*찌버거|창[녀놈]|" +
                    "[가-힣]{2,}충[^가-힣]|[가-힣]{2,}츙|" +
                    "부녀자|화냥년|환[양향]년|호[0-9]*[구모]|" +
                    "조[선센][징]|조센|" +
                    "[쪼쪽쪾](?:[발빨]이|[바빠]리)|" +
                    "盧|무현|찌끄[레래]기|(?:하악){2,}|하[앍앜]|" +
                    "[낭당랑앙항남담람암함][ ]?[가-힣]+[띠찌]|" +
                    "느[금급]마|文在|在寅|" +
                    "(?<=[^\\n])[家哥]|속냐|" +
                    "[tT]l[qQ]kf|Wls|" +
                    "[ㅂ]신|[ㅅ]발|[ㅈ]밥";

    private static final Pattern PROFANITY_PATTERN = Pattern.compile(PROFANITY_REGEX);


    public void validateNickname(String nickname){

        // 비속어 포함 닉네임
        if (PROFANITY_PATTERN.matcher(nickname).find()) {
            throw new BaseException(BaseResponseStatus.PROFANITY_NICKNAME);
        }

        // 서비스 브랜드명 포함 닉네임
        if(SERVICE_NAME_BRAND.stream().anyMatch(nickname::equalsIgnoreCase)){
            throw new BaseException(BaseResponseStatus.SERVICE_BRAND_NICKNAME);
        }

        // 운영자, 관리자 혼동 닉네임
        if (ADMIN_LIKE_WORDS.stream()
                .anyMatch(word -> nickname.toLowerCase().contains(word.toLowerCase()))) {
            throw new BaseException(BaseResponseStatus.ADMIN_MANAGER_NICKNAME);
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
