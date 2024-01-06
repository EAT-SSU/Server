package ssu.eatssu.web.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Schema(title = "메뉴 리뷰 정보(평점 등등)")
@Getter
@AllArgsConstructor
@Builder
public class MenuReviewInformationResponse {

    @Schema(description = "메뉴 리스트", example = "[\"고구마치즈돈까스\", \"막국수\", \"미니밥\", \"단무지\", \"요구르트\"]")
    private List<String> menuNames;

    @Schema(description = "리뷰 개수", example = "15")
    private Integer totalReviewCount;

    @Schema(description = "평점-메인", example = "4.4")
    private Double mainRate;

    @Schema(description = "평점-양", example = "4.4")
    private Double amountRate;

    @Schema(description = "평점-맛", example = "4.4")
    private Double tasteRate;

    private ReviewRateCount reviewRateCount;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ReviewRateCount {

        @Schema(description = "평점 5점 개수", example = "5")
        private Long rateFiveCount;

        @Schema(description = "평점 4점 개수", example = "5")
        private Long rateFourCount;

        @Schema(description = "평점 3점 개수", example = "5")
        private Long rateThreeCount;

        @Schema(description = "평점 2점 개수", example = "5")
        private Long rateTwoCount;

        @Schema(description = "평점 1점 개수", example = "5")
        private Long rateOneCount;

        public static ReviewRateCount from(Map<Integer, Long> rateCount) {
            return ReviewRateCount.builder()
	.rateOneCount(rateCount.get(1))
	.rateTwoCount(rateCount.get(2))
	.rateThreeCount(rateCount.get(3))
	.rateFourCount(rateCount.get(4))
	.rateFiveCount(rateCount.get(5)).build();
        }
    }

}
