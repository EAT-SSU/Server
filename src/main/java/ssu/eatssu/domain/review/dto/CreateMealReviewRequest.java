package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewImage;
import ssu.eatssu.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Schema(title = "리뷰 작성")
@Getter
@AllArgsConstructor
public class CreateMealReviewRequest {
    @Schema(description = "식단 식별자", example = "123")
    private Long mealId;
    @Schema(description = "평점", example = "4")
    private Integer rating;
    private List<MenuLikeRequest> menuLikes;
    @Schema(description = "한줄평", example = "맛있어용")
    private String content;
    @Schema(description = "리뷰 이미지 url 리스트", example = "[\"imgurl1\", \"imgurl2\"]")
    private List<String> imageUrls;

    public Review toReviewEntity(User user, Meal meal) {
        return Review.builder()
                     .user(user)
                     .meal(meal)
                     .content(content)
                     .rating(rating)
                     .build();
    }

    public List<ReviewImage> createReviewImages(Review review) {
        return imageUrls.stream()
                        .map(imageUrl -> new ReviewImage(review, imageUrl))
                        .collect(Collectors.toList());
    }
}
