package ssu.eatssu.domain.review.dto;

public record MenuIdNameLikeDto(
        Long id,
        String name,
        Boolean isLike
) {
}
