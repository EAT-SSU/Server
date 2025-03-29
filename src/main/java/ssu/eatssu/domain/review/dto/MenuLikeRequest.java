package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(title = "메뉴 좋아요/싫어요 리뷰")
@Getter
@AllArgsConstructor
public class MenuLikeRequest {
	@Schema(description = "메뉴 식별자", example = "123")
	private Long menuId;
	@Schema(description = "좋아요 or 싫어요", example = "좋아요 : true or 싫어요 : false")
	private Boolean isLike;
}
