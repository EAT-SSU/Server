package ssu.eatssu.web.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.domain.Review;
import ssu.eatssu.domain.User;

@Schema(title = "리뷰 작성")
@NoArgsConstructor
@Getter
public class ReviewCreate {

    @Schema(description = "평점-메인", example = "4")
    private Integer mainGrade;

    @Schema(description = "평점-양", example = "4")
    private Integer amountGrade;

    @Schema(description = "평점-맛", example = "4")
    private Integer tasteGrade;

    @Max(150)
    @Schema(description = "한줄평", example = "맛있어용")
    private String content;

    public Review toEntity(User user, Menu menu) {
        return Review.builder()
                .user(user).content(this.content).mainGrade(this.mainGrade).amountGrade(this.amountGrade)
                .tasteGrade(this.tasteGrade).menu(menu).build();
    }

}
