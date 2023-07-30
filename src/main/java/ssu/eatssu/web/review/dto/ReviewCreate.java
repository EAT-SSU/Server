package ssu.eatssu.web.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.domain.Review;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.enums.ReviewTag;

import java.util.List;

@Schema(title = "리뷰 작성")
@NoArgsConstructor
@Getter
public class ReviewCreate {

    @Schema(description = "평점", example = "4")
    private Integer grade;

    @Schema(description = "리뷰 태그", example = "[\"GOOD\", \"BAD\"]")
    private List<ReviewTag> reviewTags;

    @Max(150)
    @Schema(description = "한줄평", example = "맛있어용")
    private String content;

    public Review toEntity(User user, Menu menu){

        Review review = Review.builder()
                .user(user).content(getContent()).grade(getGrade()).menu(menu)
                .build();
        review.setTags(getReviewTags());
        return review;
    }

}
