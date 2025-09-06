package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.user.entity.User;

@Getter
@Setter
@Schema(title = "리뷰 작성")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadReviewRequest {

    @Schema(description = "평점-메인", example = "4")
    private Integer mainRating;

    @Max(150)
    @Schema(description = "한줄평", example = "맛있어용")
    private String content;

    @Schema(description = "리뷰 이미지 URL", example = "https://s3.~~~.jpg")
    private String imageUrl;

    public UploadReviewRequest(int mainRating, String content) {
        Assert.isTrue(mainRating >= 1 && mainRating <= 5, "평점은 1에서 5 사이 여야 합니다.");
        Assert.notNull(content, "리뷰는 null이 될 수 없습니다.");
        this.mainRating = mainRating;
        this.content = content;
    }

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


