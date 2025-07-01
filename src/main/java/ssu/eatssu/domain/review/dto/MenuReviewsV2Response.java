package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.menu.entity.Menu;

@Schema(title = "메뉴 리뷰 정보V2(평점 등등)")
@Getter
@AllArgsConstructor
@Builder
public class MenuReviewsV2Response implements ReviewInformationResponse {
    @Schema(description = "메뉴명", example = "고구마 치즈 돈까스")
    private String menuName;

    @Schema(description = "리뷰 개수", example = "15")
    private Long totalReviewCount;

    @Schema(description = "평점-메인", example = "4.4")
    private Double mainRating;

    @Schema(description = "좋아요 개수", example = "4.4")
    private Integer likeCount;

    @Schema(description = "싫어요 개수", example = "4.4")
    private Integer unlikeCount;

    @Schema(description = "평점 별 갯수")
    private ReviewRatingCount reviewRatingCount;

    public static MenuReviewsV2Response of(Long totalReviewCount,
                                           String menuName,
                                           RatingAverages ratingAverages,
                                           ReviewRatingCount reviewRatingCount,
                                           Menu menu) {

        return MenuReviewsV2Response.builder()
                                    .menuName(menuName)
                                    .mainRating(ratingAverages.mainRating())
                                    .likeCount(menu.getLikeCount())
                                    .unlikeCount(menu.getUnlikeCount())
                                    .totalReviewCount(totalReviewCount)
                                    .reviewRatingCount(reviewRatingCount)
                                    .build();
    }
}
