package ssu.eatssu.web.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.domain.review.Review;
import ssu.eatssu.domain.User;

@Schema(title = "리뷰 작성")
@NoArgsConstructor
@Getter
public class CreateReviewRequest {

    @Schema(description = "평점-메인", example = "4")
    private Integer mainRate;

    @Schema(description = "평점-양", example = "4")
    private Integer amountRate;

    @Schema(description = "평점-맛", example = "4")
    private Integer tasteRate;

    @Max(150)
    @Schema(description = "한줄평", example = "맛있어용")
    private String content;

    public CreateReviewRequest(int mainRate, int amountRate, int tasteRate, String content) {
        Assert.isTrue(mainRate >= 1 && mainRate <= 5, "평점은 1에서 5 사이 여야 합니다.");
        Assert.isTrue(amountRate >= 1 && amountRate <= 5, "평점은 1에서 5 사이 여야 합니다.");
        Assert.isTrue(tasteRate >= 1 && tasteRate <= 5, "평점은 1에서 5 사이 여야 합니다.");
        Assert.notNull(content, "리뷰는 null이 될 수 없습니다.");
        this.mainRate = mainRate;
        this.amountRate = amountRate;
        this.tasteRate = tasteRate;
        this.content = content;
    }

    public Review toEntity(User user, Menu menu) {
        return Review.builder()
            .user(user).content(this.content).mainRate(this.mainRate).amountRate(this.amountRate)
            .tasteRate(this.tasteRate).menu(menu).build();
    }

}
