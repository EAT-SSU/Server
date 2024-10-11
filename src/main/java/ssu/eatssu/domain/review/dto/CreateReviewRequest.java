package ssu.eatssu.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewImage;
import ssu.eatssu.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class CreateReviewRequest {
    private Long mealId;
    private Integer rating;
    private String content;
    private List<String> imageUrls;
    private List<MenuLikeRequest> menuLikes;

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