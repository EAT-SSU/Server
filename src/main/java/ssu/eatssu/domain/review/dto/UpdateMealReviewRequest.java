package ssu.eatssu.domain.review.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(title = "리뷰 수정(글)")
@Getter
@AllArgsConstructor
public class UpdateMealReviewRequest {
	@Schema(description = "평점", example = "4")
	private Integer rating;
	private List<MenuLikeRequest> menuLikes;
	@Max(150)
	@Schema(description = "한줄평", example = "맛있어용")
	private String content;
}
