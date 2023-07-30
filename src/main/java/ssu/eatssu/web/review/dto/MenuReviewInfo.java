package ssu.eatssu.web.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(title = "메뉴 리뷰 정보(평점 등등)")
@Getter
@AllArgsConstructor
@Builder
public class MenuReviewInfo {
    @Schema(description = "메뉴 이름", example = "김치볶음밥 & 계란국")
    private String menuName;

    @Schema(description = "리뷰 개수", example = "15")
    private Integer totalReviewCount;

    @Schema(description = "평균 평점", example = "4.2")
    private Double grade;

    private ReviewGradeCnt reviewGradeCnt;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ReviewGradeCnt{
        @Schema(description = "평점5 개수", example = "5")
        private Long fiveCnt;

        @Schema(description = "평점4 개수", example = "5")
        private Long fourCnt;

        @Schema(description = "평점3 개수", example = "5")
        private Long threeCnt;

        @Schema(description = "평점2 개수", example = "5")
        private Long twoCnt;

        @Schema(description = "평점1 개수", example = "5")
        private Long oneCnt;
    }

}
