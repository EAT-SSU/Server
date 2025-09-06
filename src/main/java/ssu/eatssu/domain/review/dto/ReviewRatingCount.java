package ssu.eatssu.domain.review.dto;

import ssu.eatssu.domain.review.entity.Review;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

//COMMENT: 더 좋은 매개변수 명이 있을 것 같은데 제 머리로는 이게 한계입니다... 멋진 변수명으로 바꿔주세요
public record ReviewRatingCount(long oneStarCount, long twoStarCount, long threeStarCount, long fourStarCount,
                                long fiveStarCount) {

    public ReviewRatingCount {
    }

    public static ReviewRatingCount from(List<Review> reviews) {
        Map<Integer, Long> countsByStar = reviews.stream()
                                                 .map(r -> {
                                                     Integer main = (r.getRatings() != null) ? r.getRatings()
                                                                                                .getMainRating() : null;
                                                     return (main != null) ? main : r.getRating();
                                                 })
                                                 .filter(Objects::nonNull)
                                                 .collect(Collectors.groupingBy(Integer::intValue,
                                                                                Collectors.counting()));

        return new ReviewRatingCount(
                countsByStar.getOrDefault(1, 0L),
                countsByStar.getOrDefault(2, 0L),
                countsByStar.getOrDefault(3, 0L),
                countsByStar.getOrDefault(4, 0L),
                countsByStar.getOrDefault(5, 0L)
        );
    }
}
