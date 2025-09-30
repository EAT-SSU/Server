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

    public static boolean isExcludedFromReview(String menuName) {
        if (menuName == null || menuName.isBlank()) return true;

        menuName = menuName.trim();

        for (String keyword : EXCLUDED_KEYWORDS) {
            if (menuName.contains(keyword)) {
                return true;
            }
        }

        if (menuName.contains("밥")
                && !menuName.contains("자장밥")
                && !menuName.contains("볶음밥")
                && !menuName.contains("비빔밥")) {
            return true;
        }

        if (menuName.contains("김치")
                && !menuName.contains("김치볶음밥")) {
            return true;
        }

        return SAUCE_PATTERN.matcher(menuName).matches();
    }
}
