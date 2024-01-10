package ssu.eatssu.web.review.dto;

import lombok.Builder;

public record RatingAverages(Double mainRating, Double amountRating, Double tasteRating) {

    @Builder
    public RatingAverages {
    }
}