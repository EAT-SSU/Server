package ssu.eatssu.domain.review.dto.response;

public record MenuIdNameLikeDto(
        Long id,
        String name,
        Boolean isLike
) {
}
