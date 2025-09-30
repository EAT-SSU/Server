package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.user.entity.User;

@Getter
@Setter
@Schema(title = "menu에 대한 리뷰 작성")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateMenuReviewRequest {
    @Schema(description = "메뉴 식별자", example = "123")
    private Long menuId;
    @Schema(description = "평점-메인", example = "4")
    private Integer mainRating;
    @Max(150)
    @Schema(description = "한줄평", example = "맛있어용")
    private String content;

    @Schema(description = "리뷰 이미지 URL", example = "https://s3.~~~.jpg")
    private String imageUrl;
    private MenuLikeRequest menuLike;

    public Review toReviewEntity(User user, Menu menu) {
        Ratings ratings = Ratings.of(this.mainRating);
        return Review.builder()
                     .user(user)
                     .content(this.content)
                     .ratings(ratings)
                     .menu(menu)
                     .build();
    }
}
