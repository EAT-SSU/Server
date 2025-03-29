package ssu.eatssu.domain.review.dto;

import lombok.Builder;

public record RatingAverages(Double mainRating, Double amountRating, Double tasteRating) {

	@Builder
	public RatingAverages {
	}
}