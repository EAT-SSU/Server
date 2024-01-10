package ssu.eatssu.web.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import ssu.eatssu.domain.menu.Menu;

@Schema(title = "메뉴 리뷰 정보(평점 등등)")
@Getter
@AllArgsConstructor
@Builder
public class MenuReviewInformationResponse implements ReviewInformationResponse {

    @Schema(description = "메뉴 리스트", example = "[\"고구마치즈돈까스\", \"막국수\", \"미니밥\", \"단무지\", \"요구르트\"]")
    private String menuName;

    @Schema(description = "리뷰 개수", example = "15")
    private Integer totalReviewCount;

    @Schema(description = "평점-메인", example = "4.4")
    private Double mainRating;

    @Schema(description = "평점-양", example = "4.4")
    private Double amountRating;

    @Schema(description = "평점-맛", example = "4.4")
    private Double tasteRating;
    private ReviewRatingCount reviewRatingCount;

    public static MenuReviewInformationResponse of(Menu menu,
        RatingAverages ratingAverages,
        ReviewRatingCount reviewRatingCount) {
        return MenuReviewInformationResponse.builder()
            .menuName(menu.getName())
            .totalReviewCount(menu.getTotalReviewCount())
            .mainRating(ratingAverages.mainRating())
            .amountRating(ratingAverages.amountRating())
            .tasteRating(ratingAverages.tasteRating())
            .reviewRatingCount(reviewRatingCount)
            .build();
    }
}
