package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.menu.entity.Menu;

@Schema(title = "메뉴 리뷰 정보(평점 등등)")
@Getter
@AllArgsConstructor
@Builder
public class MenuReviewResponse implements ReviewInformationResponse {

    @Schema(description = "메뉴 이름", example = "바질토마토베이글")
    private String menuName;

    @Schema(description = "리뷰 개수", example = "15")
    private Integer totalReviewCount;

    @Schema(description = "평점-메인", example = "4.4")
    private Double mainRating;

    @Schema(description = "평점-양", example = "4.4")
    private Double amountRating;

    @Schema(description = "평점-맛", example = "4.4")
    private Double tasteRating;

    @Schema(description = "평점 별 갯수")
    private ReviewRatingCount reviewRatingCount;

    public static MenuReviewResponse of(Menu menu,
                                        RatingAverages ratingAverages,
                                        ReviewRatingCount reviewRatingCount) {
        return MenuReviewResponse.builder()
                .menuName(menu.getName())
                .totalReviewCount(menu.getTotalReviewCount())
                .mainRating(ratingAverages.mainRating())
                .amountRating(ratingAverages.amountRating())
                .tasteRating(ratingAverages.tasteRating())
                .reviewRatingCount(reviewRatingCount)
                .build();
    }
}
