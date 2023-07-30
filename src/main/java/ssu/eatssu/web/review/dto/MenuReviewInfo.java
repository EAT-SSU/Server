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
public class MenuReviewInfo {
    @Schema(description = "메뉴 리스트", example = "[\"고구마치즈돈까스\", \"막국수\", \"미니밥\", \"단무지\", \"요구르트\"]")
    private List<String> menuName;

    @Schema(description = "리뷰 개수", example = "15")
    private Integer totalReviewCount;

    @Schema(description = "평점-메인", example = "4.4")
    private Double mainGrade;

    @Schema(description = "평점-양", example = "4.4")
    private Double amountGrade;

    @Schema(description = "평점-맛", example = "4.4")
    private Double tasteGrade;

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

        public static ReviewGradeCnt fromMap (Map<Integer, Long> gradeCntMap){
            return ReviewGradeCnt.builder()
                    .oneCnt(gradeCntMap.get(1)).twoCnt(gradeCntMap.get(2)).threeCnt(gradeCntMap.get(3))
                    .fourCnt(gradeCntMap.get(4)).fiveCnt(gradeCntMap.get(5)).build();
        }
    }

}
