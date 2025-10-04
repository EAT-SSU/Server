package ssu.eatssu.domain.review.utils;

import java.util.Set;
import java.util.regex.Pattern;

public class MenuFilterUtil {

    private static final Set<String> EXCLUDED_KEYWORDS = Set.of(
            "작은밥", "쌀밥", "잡곡밥", "찰흑미밥", "귀리밥", "백미밥",
            "깍두기", "열무김치", "포기김치", "맛김치", "배추김치",
            "단무지", "오이피클", "야쿠르트", "요구르트", "우동국물"
                                                               );

    private static final Pattern SAUCE_PATTERN = Pattern.compile(".*소스.*");
    private static final Pattern SINGLE_KIMCHI_PATTERN = Pattern.compile("(^|\\s)김치($|\\s)");
    private static final Pattern SINGLE_BAP_PATTERN = Pattern.compile("(^|\\s)밥($|\\s)");

    public static boolean isExcludedFromReview(String menuName) {
        if (menuName == null || menuName.isBlank()) return true;
        menuName = menuName.trim();

        // 여러 메뉴가 한 줄에 있을 수 있으니 split
        String[] items = menuName.split("[+,&/\\s]+");

        boolean allExcluded = true;
        for (String item : items) {
            String trimmed = item.trim();
            if (trimmed.isBlank()) continue;

            if (!isExcludedSingle(trimmed)) {
                allExcluded = false;
                break;
            }
        }

        return allExcluded;
    }

    private static boolean isExcludedSingle(String name) {
        for (String keyword : EXCLUDED_KEYWORDS) {
            if (name.equals(keyword)) return true;
        }

        if (SINGLE_KIMCHI_PATTERN.matcher(name).find()) return true;
        if (SINGLE_BAP_PATTERN.matcher(name).find()) return true;

        return SAUCE_PATTERN.matcher(name).matches();
    }
}
