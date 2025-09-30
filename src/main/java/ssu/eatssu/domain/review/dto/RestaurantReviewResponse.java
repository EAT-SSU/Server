package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(title = "특정 식당 모든 리뷰 정보(평점 등등)")
@Getter
@AllArgsConstructor
@Builder
public class RestaurantReviewResponse {
    @Schema(description = "리뷰 개수", example = "15")
    private Integer totalReviewCount;

    @Schema(description = "별점 별 개수")
    private ReviewRatingCount reviewRatingCount;

    @Schema(description = "리뷰 평점", example = "4.4")
    private Double rating;

    @Schema(description = "좋아요 개수", example = "15")
    private Integer likeCount;

    @Schema(description = "싫어요 개수", example = "15")
    private Integer unlikeCount;
}