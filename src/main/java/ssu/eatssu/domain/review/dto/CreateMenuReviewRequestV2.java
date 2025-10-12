package ssu.eatssu.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewImage;
import ssu.eatssu.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Schema(title = "menu에 대한 리뷰 작성 (리뷰v2)")
@Getter
@AllArgsConstructor
public class CreateMenuReviewRequestV2 {
    @Schema(description = "평점", example = "5")
    @Min(1)
    @Max(5)
    private Integer rating;

    private MenuLikeRequest menuLike;

    @Schema(description = "한줄평", example = "이 메뉴 진짜 맛있어요!")
    private String content;

    @Schema(description = "리뷰 이미지 URL 리스트", example = "[\"imgurl1\", \"imgurl2\"]")
    private List<String> imageUrls;

    public Review toReviewEntity(User user, Menu menu) {
        return Review.builder()
                     .user(user)
                     .menu(menu)
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
