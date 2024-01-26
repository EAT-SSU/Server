package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;

@Schema(title = "리뷰 수정(글)")
public record ReviewUpdateRequest(

    @Schema(description = "평점-메인", example = "4")
    Integer mainRate,

    @Schema(description = "평점-양", example = "4")
    Integer amountRate,

    @Schema(description = "평점-맛", example = "4")
    Integer tasteRate,

    @Max(150)
    @Schema(description = "한줄평", example = "맛있어용")
    String content
) {

}