package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(title = "식단 리뷰 정보(평점 등등)")
@Getter
@AllArgsConstructor
@Builder
public class MealReviewInformationResponse implements ReviewInformationResponse {

    @Schema(description = "메뉴명 리스트", example = "[\"고구마치즈돈까스\", \"막국수\", \"미니밥\", \"단무지\", \"요구르트\"]")
    private List<String> menuNames;

    @Schema(description = "리뷰 개수", example = "15")
    private Integer totalReviewCount;

    @Schema(description = "평점-메인", example = "4.4")
    private Double mainRating;

    @Schema(description = "평점-양", example = "4.4")
    private Double amountRating;

    @Schema(description = "평점-맛", example = "4.4")
    private Double tasteRating;

    private ReviewRatingCount reviewRatingCount;

    public static MealReviewInformationResponse of(Integer totalReviewCount, List<String> menuNames,
        RatingAverages ratingAverages, ReviewRatingCount reviewRatingCount) {

        return MealReviewInformationResponse.builder()
            .menuNames(menuNames)
            .mainRating(ratingAverages.mainRating())
            .amountRating(ratingAverages.amountRating())
            .tasteRating(ratingAverages.tasteRating())
            .totalReviewCount(totalReviewCount)
            .reviewRatingCount(reviewRatingCount)
            .build();
    }
}
