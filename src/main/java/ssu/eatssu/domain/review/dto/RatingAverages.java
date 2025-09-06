package ssu.eatssu.domain.review.dto;

import lombok.Builder;

public record RatingAverages(Double mainRating) {

    @Builder
    public RatingAverages {
    }
}