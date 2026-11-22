package ssu.eatssu.domain.menu.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MenuNameSanitizerTest {

    @ParameterizedTest
    @MethodSource("nonMenuNames")
    void sanitizeExcludesNonMenuNames(String raw, MenuNameExcludeReason expectedReason) {
        // given & when
        SanitizedMenuName result = MenuNameSanitizer.sanitize(raw);

        // then
        assertThat(result.isExcluded()).isTrue();
        assertThat(result.excludeReason()).isEqualTo(expectedReason);
    }

    @ParameterizedTest
    @MethodSource("noisyMenuNames")
    void sanitizeNormalizesNoisyMenuNames(String raw, String expected) {
        // given & when
        SanitizedMenuName result = MenuNameSanitizer.sanitize(raw);

        // then
        assertThat(result.isExcluded()).isFalse();
        assertThat(result.name()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("normalMenuNames")
    void sanitizeKeepsNormalMenuNamesUnchanged(String raw) {
        // given & when
        SanitizedMenuName result = MenuNameSanitizer.sanitize(raw);

        // then
        assertThat(result.isExcluded()).isFalse();
        assertThat(result.name()).isEqualTo(raw);
    }

    private static Stream<Arguments> nonMenuNames() {
        return Stream.of(
                Arguments.of("즐거운 명절 보내세요..^^", MenuNameExcludeReason.ELLIPSIS),
                Arguments.of("밝은 보름달처럼…", MenuNameExcludeReason.ELLIPSIS),
                Arguments.of("부드러운 순두부와 차돌박이를…~!", MenuNameExcludeReason.ELLIPSIS),
                Arguments.of("밝은 보름달처럼 넉넉한 한가위 보내세요.", MenuNameExcludeReason.SENTENCE),
                Arguments.of("즐거운 추석명절 보내세세요. ^^", MenuNameExcludeReason.SENTENCE),
                Arguments.of("즐거운 명절 보내세요~", MenuNameExcludeReason.SENTENCE),
                Arguments.of("맛있게 드세요!", MenuNameExcludeReason.SENTENCE),
                Arguments.of("양해 부탁드립니다", MenuNameExcludeReason.NOTICE),
                Arguments.of("양해 부탁드립니다.", MenuNameExcludeReason.NOTICE),
                Arguments.of("원산지: 쌀(국내산), 돼지고기(국내산)", MenuNameExcludeReason.NOTICE),
                Arguments.of("알러지유발식품: 대두, 밀, 우유", MenuNameExcludeReason.NOTICE),
                Arguments.of("알레르기 정보", MenuNameExcludeReason.NOTICE),
                Arguments.of("새해 ", MenuNameExcludeReason.GREETING),
                Arguments.of("한가위", MenuNameExcludeReason.GREETING),
                Arguments.of("福 설날", MenuNameExcludeReason.GREETING),
                Arguments.of("(국내산)", MenuNameExcludeReason.TAG_ONLY),
                Arguments.of("(쌈밥데이)", MenuNameExcludeReason.TAG_ONLY),
                Arguments.of("(추석)", MenuNameExcludeReason.GREETING),
                Arguments.of("福(복)", MenuNameExcludeReason.TAG_ONLY),
                Arguments.of("많이 받으세요.", MenuNameExcludeReason.SENTENCE),
                Arguments.of("쌀:국내산", MenuNameExcludeReason.ORIGIN_ONLY),
                Arguments.of("-", MenuNameExcludeReason.EMPTY),
                Arguments.of(".", MenuNameExcludeReason.EMPTY),
                Arguments.of("..", MenuNameExcludeReason.EMPTY),
                Arguments.of("★", MenuNameExcludeReason.EMPTY),
                Arguments.of("", MenuNameExcludeReason.EMPTY),
                Arguments.of("　 ", MenuNameExcludeReason.EMPTY),
                Arguments.of(null, MenuNameExcludeReason.EMPTY),
                Arguments.of("6.0", MenuNameExcludeReason.NO_HANGUL),
                Arguments.of("(Boneless, gyozas)", MenuNameExcludeReason.NO_HANGUL),
                Arguments.of("dakgalbi", MenuNameExcludeReason.NO_HANGUL),
                Arguments.of("yukgaejang", MenuNameExcludeReason.NO_HANGUL),
                Arguments.of("T-bone Steak", MenuNameExcludeReason.NO_HANGUL),
                Arguments.of("Andong Braised Chicken", MenuNameExcludeReason.NO_HANGUL),
                Arguments.of("돈까스".repeat(11), MenuNameExcludeReason.TOO_LONG)
        );
    }

    private static Stream<Arguments> noisyMenuNames() {
        return Stream.of(
                Arguments.of("연두부   …", "연두부"),
                Arguments.of("연두부                                                                 ", "연두부"),
                Arguments.of("쌀밥 ", "쌀밥"),
                Arguments.of("잔치국수(大)  ", "잔치국수(大)"),
                Arguments.of(".베이컨볶음밥", "베이컨볶음밥"),
                Arguments.of(".콩나물국", "콩나물국"),
                Arguments.of(".수제비국", "수제비국"),
                Arguments.of("★차돌짬뽕", "차돌짬뽕"),
                Arguments.of("★미역국 & 뼈없는닭갈비", "미역국 & 뼈없는닭갈비"),
                Arguments.of("　쌀밥 ", "쌀밥"),
                Arguments.of("\t· 김치찌개", "김치찌개"),
                Arguments.of("* 돈까스...", "돈까스")
        );
    }

    private static Stream<String> normalMenuNames() {
        return Stream.of(
                "잔치국수(大)",
                "잔치국수(대)",
                "물냉면",
                "떡~만두국(大)",
                "멸치~잔치국수(大)",
                "시원한~물냉면",
                "치킨마요",
                "참치마요덮밥",
                "크림소다",
                "(쌀밥데이)콩나물국",
                "(쌈밥데이)콩나물국",
                "(초등)토마토스파게티",
                "(大)쌀밥",
                "(大)-추가밥",
                "[포장] 샐러드파스타",
                "뼈없는닭갈비(순닭/브라질산)",
                "뼈 없는 닭갈비",
                "탄산음료(콜라 또는 사이다 중 선택1)",
                "등심돈까스 * 사천식볶음우동",
                "콘크림함박스테이크 & 베이컨마늘쫑필라프",
                "토스트 & 딸기쨈, 버터",
                "삶은 만두 네 개",
                "한가위 특식",
                "추석특선 갈비찜",
                "감자샐러드"
        );
    }

    @ParameterizedTest
    @MethodSource("outOfScopeMenuNames")
    void sanitizeLeavesOutOfScopeVariantsUntouched(String raw) {
        // given & when
        SanitizedMenuName result = MenuNameSanitizer.sanitize(raw);

        // then
        assertThat(result.isExcluded()).isFalse();
        assertThat(result.name()).isEqualTo(raw);
    }

    private static Stream<String> outOfScopeMenuNames() {
        return Stream.of(
                "칠리s",
                "허니s",
                "사우전s",
                "타르s",
                "데미s",
                "후르츠s",
                "무지방오리엔탈D",
                "오징어가스/사우전",
                "시원한~물냉면",
                "멸치~잔치국수",
                "떡~만두국(大)",
                "웨지감자튀김*케찹",
                "웨지감자튀김&케찹",
                "웨지감자튀김+케찹",
                "수육*새우젓",
                "수육&새우젓",
                "수육+새우젓",
                "잔치국수(대)",
                "잔치국수(大)",
                "꼬막 비빔밥 & 계란후라이",
                "꼬막비빔밥 & 계란후라이"
        );
    }
}
