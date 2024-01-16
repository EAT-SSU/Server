package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "리뷰 수정(글)")
@NoArgsConstructor
@Getter
public class UpdateReviewRequest {

    @Schema(description = "평점-메인", example = "4")
    private Integer mainRate;

    @Schema(description = "평점-양", example = "4")
    private Integer amountRate;

    @Schema(description = "평점-맛", example = "4")
    private Integer tasteRate;

    @Max(150)
    @Schema(description = "한줄평", example = "맛있어용")
    private String content;
}