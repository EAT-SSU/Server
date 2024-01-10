package ssu.eatssu.web.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;
import ssu.eatssu.domain.menu.Menu;
import ssu.eatssu.domain.rating.Ratings;
import ssu.eatssu.domain.review.Review;
import ssu.eatssu.domain.user.User;

@Schema(title = "리뷰 작성")
@NoArgsConstructor
@Getter
public class CreateReviewRequest {

    @Schema(description = "평점-메인", example = "4")
    private Integer mainRating;

    @Schema(description = "평점-양", example = "4")
    private Integer amountRating;

    @Schema(description = "평점-맛", example = "4")
    private Integer tasteRating;

    @Max(150)
    @Schema(description = "한줄평", example = "맛있어용")
    private String content;

    public CreateReviewRequest(int mainRating, int amountRating, int tasteRating, String content) {
        Assert.isTrue(mainRating >= 1 && mainRating <= 5, "평점은 1에서 5 사이 여야 합니다.");
        Assert.isTrue(amountRating >= 1 && amountRating <= 5, "평점은 1에서 5 사이 여야 합니다.");
        Assert.isTrue(tasteRating >= 1 && tasteRating <= 5, "평점은 1에서 5 사이 여야 합니다.");
        Assert.notNull(content, "리뷰는 null이 될 수 없습니다.");
        this.mainRating = mainRating;
        this.amountRating = amountRating;
        this.tasteRating = tasteRating;
        this.content = content;
    }

    public Review toEntity(User user, Menu menu) {
        Ratings ratings = Ratings.of(this.mainRating, this.amountRating, this.tasteRating);
        return Review.builder()
                .user(user).content(this.content).ratings(ratings).menu(menu)
                .build();
    }

}
