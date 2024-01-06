package ssu.eatssu.web.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.vo.AverageRates;
import ssu.eatssu.vo.RateCountMap;

import java.util.List;

@Schema(title = "식단 리뷰 정보(평점 등등)")
@Getter
@AllArgsConstructor
@Builder
public class MealReviewInfo {
    @Schema(description = "메뉴명 리스트", example = "[\"고구마치즈돈까스\", \"막국수\", \"미니밥\", \"단무지\", \"요구르트\"]")
    private List<String> menuNameList;

    @Schema(description = "리뷰 개수", example = "15")
    private Integer totalReviewCount;

    @Schema(description = "평점-메인", example = "4.4")
    private Double mainRate;

    @Schema(description = "평점-양", example = "4.4")
    private Double amountRate;

    @Schema(description = "평점-맛", example = "4.4")
    private Double tasteRate;

    private MealReviewInfo.ReviewRateCnt reviewRateCnt;

    public static MealReviewInfo of(List<String> menuNameList, Integer reviewCount, AverageRates rates,
                                    RateCountMap rateCountMap) {

        return MealReviewInfo.builder()
                .menuNameList(menuNameList)
                .mainRate(rates.mainRate()).tasteRate(rates.tasteRate()).amountRate(rates.amountRate())
                .totalReviewCount(reviewCount)
                .reviewRateCnt(new ReviewRateCnt(rateCountMap))
                .build();
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ReviewRateCnt {
        @Schema(description = "평점5 개수", example = "5")
        private Integer fiveCnt;

        @Schema(description = "평점4 개수", example = "5")
        private Integer fourCnt;

        @Schema(description = "평점3 개수", example = "5")
        private Integer threeCnt;

        @Schema(description = "평점2 개수", example = "5")
        private Integer twoCnt;

        @Schema(description = "평점1 개수", example = "5")
        private Integer oneCnt;

        public ReviewRateCnt(RateCountMap rateCountMap) {
            this.oneCnt = rateCountMap.get(1);
            this.twoCnt = rateCountMap.get(2);
            this.threeCnt = rateCountMap.get(3);
            this.fourCnt = rateCountMap.get(4);
            this.fiveCnt = rateCountMap.get(5);
        }
    }
}
