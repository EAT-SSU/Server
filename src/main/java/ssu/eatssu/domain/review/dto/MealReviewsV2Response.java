package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(title = "식단 리뷰 정보V2(평점 등등)")
@Getter
@AllArgsConstructor
@Builder
public class MealReviewsV2Response implements ReviewInformationResponse {
    @Schema(description = "메뉴 리스트")
    private List<MenuIdNameDto> menuList;

    @Schema(description = "리뷰 개수", example = "15")
    private Long totalReviewCount;

    @Schema(description = "평점", example = "4.4")
    private Double rating;

    @Schema(description = "좋아요 개수", example = "4.4")
    private Integer likeCount;
    @Schema(description = "평점 별 갯수")
    private ReviewRatingCount reviewRatingCount;

    public static MealReviewsV2Response of(Long totalReviewCount, List<MenuIdNameDto> menuNames,
                                           RatingAverages ratingAverages, ReviewRatingCount reviewRatingCount) {

        return MealReviewsV2Response.builder()
                                    .menuList(menuNames)
                                    .rating(ratingAverages.mainRating())
                                    .totalReviewCount(totalReviewCount)
                                    .reviewRatingCount(reviewRatingCount)
                                    .build();
    }
}
