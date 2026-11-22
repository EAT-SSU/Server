package ssu.eatssu.domain.menu.util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class MenuNameSanitizer {

    private static final int MAX_LENGTH = 30;

    private static final Pattern EDGE_WHITESPACE =
            Pattern.compile("^[\\s\\u00A0\\u3000\\uFEFF]+|[\\s\\u00A0\\u3000\\uFEFF]+$");
    private static final Pattern TRAILING_ELLIPSIS = Pattern.compile("(?:…|\\.{2,})+$");
    private static final Pattern LEADING_SYMBOLS = Pattern.compile("^[.·ㆍ•\\-*※★☆]+");

    private static final Pattern HANGUL = Pattern.compile("\\p{IsHangul}");
    private static final Pattern INNER_WHITESPACE = Pattern.compile("[\\s\\u00A0\\u3000]");
    private static final Pattern WORD_SEPARATOR = Pattern.compile("[\\s\\u00A0\\u3000]+");
    private static final Pattern INNER_ELLIPSIS = Pattern.compile("…|\\.{2,}");
    private static final Pattern TRAILING_DECORATION = Pattern.compile("[~!?.^♥♡★☆ㅋㅎ\\s\\u00A0\\u3000]+$");
    private static final Pattern SENTENCE_PUNCTUATION_END = Pattern.compile("(?:[!?]|\\^\\^|[ㅋㅎ]+)$");

    private static final Pattern PARENTHESIS = Pattern.compile("\\([^()]*\\)");
    private static final Pattern NON_HANGUL_EXCEPT_WHITESPACE = Pattern.compile("[^\\p{IsHangul}\\s\\u00A0\\u3000]");
    private static final Pattern ORIGIN_LABEL = Pattern.compile("[가-힣]+\\s*[:：]\\s*[가-힣]{1,4}산");

    private static final Pattern DECORATION_TILDE = Pattern.compile("[~〜～]");
    private static final Pattern DECORATION_TILDE_WITH_SPACES =
            Pattern.compile("[\\s\\u00A0\\u3000]*[~〜～]+[\\s\\u00A0\\u3000]*");
    private static final Pattern CONJUNCTION = Pattern.compile("[*&+]");
    private static final Pattern KEY_WHITESPACE = Pattern.compile("[\\s\\u00A0\\u3000\\uFEFF]+");
    private static final Map<String, String> SIZE_NOTATIONS = Map.of("大", "대", "中", "중", "小", "소");

    private static final Pattern MENU_COMBINATION = Pattern.compile("[&*+/,＆＊＋／，]");
    private static final Pattern SAUCE_ABBREVIATION_MARK = Pattern.compile("\\p{IsHangul}[sSD]$");
    private static final List<String> SAUCE_SUFFIXES = List.of("소스", "드레싱");
    private static final Set<String> SAUCE_ABBREVIATION_NAMES = Set.of(
            "칠리", "허니", "사우전", "타르", "데미", "후르츠", "스리라차", "유린기", "브라운", "머스타드",
            "오렌지", "유자", "오리엔탈", "무지방오리엔탈", "데리", "치폴레", "랜치"
    );

    private static final List<String> NOTICE_KEYWORDS = List.of(
            "원산지", "알러지", "알레르기", "휴무", "휴점", "휴업", "미운영", "운영하지",
            "공지", "안내", "양해", "감사", "죄송", "부탁"
    );
    private static final List<String> SENTENCE_ENDINGS = List.of(
            "니다", "세요", "네요", "어요", "아요", "해요", "에요", "예요", "드려요", "군요", "시오"
    );
    private static final Set<String> GREETING_WORDS = Set.of("새해", "명절", "한가위", "추석", "설날");

    private MenuNameSanitizer() {
    }

    public static String normalize(String raw) {
        return removeDecorationTilde(normalizeInternal(raw).name());
    }

    public static String toKey(String raw) {
        String key = Normalizer.normalize(normalize(raw), Normalizer.Form.NFKC);
        key = DECORATION_TILDE.matcher(key).replaceAll("");
        key = CONJUNCTION.matcher(key).replaceAll("&");
        key = KEY_WHITESPACE.matcher(key).replaceAll("");
        for (Map.Entry<String, String> size : SIZE_NOTATIONS.entrySet()) {
            key = key.replace(size.getKey(), size.getValue());
        }
        if (key.isEmpty()) {
            return null;
        }
        return key.toLowerCase(Locale.ROOT);
    }

    public static SanitizedMenuName sanitize(String raw) {
        Normalized normalized = normalizeInternal(raw);
        String name = normalized.name();
        MenuNameExcludeReason reason = findExcludeReason(name, normalized.trailingEllipsisRemoved());
        if (reason == null) {
            return SanitizedMenuName.accepted(removeDecorationTilde(name));
        }
        return SanitizedMenuName.excluded(name, reason);
    }

    private static String removeDecorationTilde(String name) {
        return DECORATION_TILDE_WITH_SPACES.matcher(name).replaceAll("");
    }

    private static Normalized normalizeInternal(String raw) {
        if (raw == null) {
            return new Normalized("", false);
        }

        String current = raw;
        boolean trailingEllipsisRemoved = false;
        String previous;
        do {
            previous = current;
            current = EDGE_WHITESPACE.matcher(current).replaceAll("");
            String withoutEllipsis = TRAILING_ELLIPSIS.matcher(current).replaceAll("");
            if (!withoutEllipsis.equals(current)) {
                trailingEllipsisRemoved = true;
                current = withoutEllipsis;
            }
            current = EDGE_WHITESPACE.matcher(current).replaceAll("");
            current = LEADING_SYMBOLS.matcher(current).replaceAll("");
        } while (!current.equals(previous));

        return new Normalized(current, trailingEllipsisRemoved);
    }

    private static MenuNameExcludeReason findExcludeReason(String name, boolean trailingEllipsisRemoved) {
        if (name.isEmpty()) {
            return MenuNameExcludeReason.EMPTY;
        }
        if (!HANGUL.matcher(name).find()) {
            return MenuNameExcludeReason.NO_HANGUL;
        }
        if (NOTICE_KEYWORDS.stream().anyMatch(name::contains)) {
            return MenuNameExcludeReason.NOTICE;
        }

        String judged = stripTrailingDecoration(name);
        if (isGreetingOnly(judged)) {
            return MenuNameExcludeReason.GREETING;
        }
        if (isEllipsisSentence(name, trailingEllipsisRemoved)) {
            return MenuNameExcludeReason.ELLIPSIS;
        }
        if (isSentence(name, judged)) {
            return MenuNameExcludeReason.SENTENCE;
        }
        if (isTagOnly(name)) {
            return MenuNameExcludeReason.TAG_ONLY;
        }
        if (ORIGIN_LABEL.matcher(name).find()) {
            return MenuNameExcludeReason.ORIGIN_ONLY;
        }
        if (isSauceOnly(name)) {
            return MenuNameExcludeReason.SAUCE_ONLY;
        }
        if (name.codePointCount(0, name.length()) > MAX_LENGTH) {
            return MenuNameExcludeReason.TOO_LONG;
        }
        return null;
    }

    private static String stripTrailingDecoration(String value) {
        return TRAILING_DECORATION.matcher(value).replaceAll("");
    }

    private static boolean isEllipsisSentence(String name, boolean trailingEllipsisRemoved) {
        if (INNER_ELLIPSIS.matcher(name).find()) {
            return true;
        }
        return trailingEllipsisRemoved && INNER_WHITESPACE.matcher(name).find();
    }

    private static boolean isSentence(String name, String judged) {
        if (SENTENCE_PUNCTUATION_END.matcher(name).find()) {
            return true;
        }
        return Arrays.stream(WORD_SEPARATOR.split(judged))
                     .map(MenuNameSanitizer::stripTrailingDecoration)
                     .anyMatch(word -> SENTENCE_ENDINGS.stream().anyMatch(word::endsWith));
    }

    private static boolean isGreetingOnly(String judged) {
        List<String> words = Arrays.stream(WORD_SEPARATOR.split(NON_HANGUL_EXCEPT_WHITESPACE.matcher(judged).replaceAll("")))
                                   .filter(word -> !word.isEmpty())
                                   .toList();
        return !words.isEmpty() && GREETING_WORDS.containsAll(words);
    }

    private static boolean isSauceOnly(String name) {
        String compact = KEY_WHITESPACE.matcher(name).replaceAll("");
        if (MENU_COMBINATION.matcher(compact).find()) {
            return false;
        }
        if (SAUCE_SUFFIXES.stream().anyMatch(compact::endsWith)) {
            return true;
        }
        if (!SAUCE_ABBREVIATION_MARK.matcher(compact).find()) {
            return false;
        }
        return SAUCE_ABBREVIATION_NAMES.contains(compact.substring(0, compact.length() - 1));
    }

    private static boolean isTagOnly(String name) {
        if (!PARENTHESIS.matcher(name).find()) {
            return false;
        }
        String outsideParenthesis = PARENTHESIS.matcher(name).replaceAll("");
        return !HANGUL.matcher(outsideParenthesis).find();
    }

    private record Normalized(String name, boolean trailingEllipsisRemoved) {
    }
}
