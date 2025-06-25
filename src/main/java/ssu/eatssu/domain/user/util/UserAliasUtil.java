package ssu.eatssu.domain.user.util;

import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.user.entity.User;

import java.util.List;
import java.util.Objects;

public class UserAliasUtil {
    public static String getUserAlias(User user, List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return "미슈테리 미식가";
        }
        double avg = reviews.stream()
                            .map(Review::getRating)
                            .filter(Objects::nonNull)
                            .mapToInt(Integer::intValue)
                            .average()
                            .orElse(0.0);

        int avgRating = (int) Math.round(avg);
        switch (avgRating) {
            case 1:
                return "깐깐슈";
            case 2:
                return "아이슈";
            case 3:
                return "밸런슈";
            case 4:
                return "딱좋슈";
            case 5:
                return "다 맛있슈";
            default:
                return "미슈테리 미식가";
        }
    }
}
