package ssu.eatssu.domain.review.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_MEAL;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_MENU;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.persistence.MealMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.dto.CreateMealReviewRequest;
import ssu.eatssu.domain.review.dto.MenuLikeRequest;
import ssu.eatssu.domain.review.dto.RestaurantReviewResponse;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewImage;
import ssu.eatssu.domain.review.entity.ReviewMenuLike;
import ssu.eatssu.domain.review.repository.ReviewImageRepository;
import ssu.eatssu.domain.review.repository.ReviewMenuLikeRepository;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

@RequiredArgsConstructor
@Service
public class MealReviewService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;
    private final MealMenuRepository mealMenuRepository;
    private final ReviewMenuLikeRepository reviewMenuLikeRepository;

    /**
     * 리뷰 생성
     */
    @Transactional
    public void createReview(CustomUserDetails userDetails, CreateMealReviewRequest request) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Meal meal = mealRepository.findById(request.getMealId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_MEAL));

        Review review = request.toReviewEntity(user, meal);
        reviewRepository.save(review);

        List<ReviewImage> reviewImages = request.createReviewImages(review);
        reviewImageRepository.saveAll(reviewImages);

        for (MenuLikeRequest menuLikes : request.getMenuLikes()) {
            Menu menu = menuRepository.findById(menuLikes.getMenuId())
                    .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

            // 리뷰-메뉴 좋아요 정보 저장
            ReviewMenuLike reviewMenuLike = ReviewMenuLike.builder()
                    .review(review)
                    .menu(menu)
                    .isLike(menuLikes.getIsLike()).build();
            reviewMenuLikeRepository.save(reviewMenuLike);

            // 메뉴에 좋아요/싫어요 수 반영
            if (menuLikes.getIsLike()) {
                menu.increaseLikeCount();
            }
            else {
                menu.increaseUnlikeCount();
            }
        }
    }

    /**
     * 특정 식당 리뷰 조회
     */
    public RestaurantReviewResponse findRestaurantReviews(Restaurant restaurant) {
        List<Meal> meals = mealRepository.findByRestaurant(restaurant);

        List<Review> reviews = reviewRepository.findByMealIn(meals);

        Integer reviewCount = reviews.size();

        Double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        List<Menu> menus = mealMenuRepository.findMenusByMeals(meals);

        Integer likeCount = menus.stream()
                .mapToInt(Menu::getLikeCount)
                .sum();

        Integer unlikeCount = menus.stream()
                .mapToInt(Menu::getUnlikeCount)
                .sum();

        Map<Integer, Long> ratingDistribution = reviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, LinkedHashMap::new, Collectors.counting()));

        ReviewRatingCount reviewRatingCount = ReviewRatingCount.from(ratingDistribution);

        return RestaurantReviewResponse.builder()
                .totalReviewCount(reviewCount)
                .reviewRatingCount(reviewRatingCount)
                .mainRating(averageRating)
                .likeCount(likeCount)
                .unlikeCount(unlikeCount)
                .build();
    }
}
