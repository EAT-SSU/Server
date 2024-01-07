package ssu.eatssu.web.review.dto;

import lombok.Builder;

public record AverageReviewRateResponse(Double mainRate, Double amountRate, Double tasteRate) {

    @Builder
    public AverageReviewRateResponse {
    }
}