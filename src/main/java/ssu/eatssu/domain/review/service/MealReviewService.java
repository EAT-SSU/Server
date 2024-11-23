package ssu.eatssu.domain.review.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_MEAL;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_MENU;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_REVIEW;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.REVIEW_PERMISSION_DENIED;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
import ssu.eatssu.domain.review.dto.MealReviewResponse;
import ssu.eatssu.domain.review.dto.MenuLikeRequest;
import ssu.eatssu.domain.review.dto.RestaurantReviewResponse;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;
import ssu.eatssu.domain.review.dto.UpdateMealReviewRequest;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewImageRepository;
import ssu.eatssu.domain.review.repository.ReviewMenuLikeRepository;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.user.dto.MyMealReviewResponse;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

@RequiredArgsConstructor
@Service
public class MealReviewService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;
    private final MealMenuRepository mealMenuRepository;

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

        request.getImageUrls().forEach(review::addReviewImage);

        for (MenuLikeRequest menuLike : request.getMenuLikes()) {
            Menu menu = menuRepository.findById(menuLike.getMenuId())
                    .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));
            review.addReviewMenuLike(menu, menuLike.getIsLike());
        }

        reviewRepository.save(review);
    }

    /**
     * 특정 식당 리뷰 조회
     */
    public RestaurantReviewResponse findRestaurantReviews(Restaurant restaurant) {
        List<Meal> meals = mealRepository.findByRestaurant(restaurant);
        List<Review> reviews = reviewRepository.findByMealIn(meals);
        List<Menu> menus = mealMenuRepository.findMenusByMeals(meals);

        Double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        Integer likeCount = menus.stream()
                .mapToInt(Menu::getLikeCount)
                .sum();

        Integer unlikeCount = menus.stream()
                .mapToInt(Menu::getUnlikeCount)
                .sum();

        ReviewRatingCount reviewRatingCount = ReviewRatingCount.from(reviews);

        return RestaurantReviewResponse.builder()
                .totalReviewCount(reviews.size())
                .reviewRatingCount(reviewRatingCount)
                .mainRating(averageRating)
                .likeCount(likeCount)
                .unlikeCount(unlikeCount)
                .build();
    }

    /**
     * 특정 식단 리뷰 리스트 조회
     */
    public SliceResponse<MealReviewResponse> findReviews(Long mealId, Long lastReviewId, Pageable pageable,
            CustomUserDetails userDetails) {
        if (!mealRepository.existsById(mealId)) {
            throw new BaseException(NOT_FOUND_MEAL);
        }

        List<Long> menuIds = mealMenuRepository.findMenuIdsByMealId(mealId);
        if (menuIds.isEmpty()) {
            return SliceResponse.empty();
        }

        List<Long> mealIds = mealMenuRepository.findMealIdsByMenuIds(menuIds);
        if (mealIds.isEmpty()) {
            return SliceResponse.empty();
        }

        Page<Review> pageReviews = reviewRepository.findReviewsByMealIds(mealIds, lastReviewId, pageable);

        List<MealReviewResponse> mealReviewResponses =
                pageReviews.getContent().stream().map(review -> MealReviewResponse.from(review,
                        userDetails.getId())).collect(Collectors.toList());

        return SliceResponse.<MealReviewResponse>builder()
                .numberOfElements(pageReviews.getNumberOfElements())
                .hasNext(pageReviews.hasNext())
                .dataList(mealReviewResponses)
                .build();
    }

    /**
     * 리뷰 수정
     */
    @Transactional
    public void updateReview(CustomUserDetails userDetails, Long reviewId, UpdateMealReviewRequest request) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_REVIEW));

        if (review.isNotWrittenBy(user)) {
            throw new BaseException(REVIEW_PERMISSION_DENIED);
        }

        Map<Menu, Boolean> menuLikes = request.getMenuLikes().stream()
                .collect(Collectors.toMap(menuLike -> menuRepository.findById(menuLike.getMenuId())
                                            .orElseThrow(() -> new BaseException(NOT_FOUND_MENU)),
                                        MenuLikeRequest::getIsLike));

        review.update(request.getContent(), request.getRating(), menuLikes);
        reviewRepository.save(review);
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public void deleteReview(CustomUserDetails userDetails, Long reviewId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_REVIEW));

        if (review.isNotWrittenBy(user)) {
            throw new BaseException(REVIEW_PERMISSION_DENIED);
        }

        review.resetMenuLikes();
        reviewRepository.delete(review);
    }

    /**
     * 내 리뷰 리스트 조회
     */
    public SliceResponse<MyMealReviewResponse> findMyReviews(CustomUserDetails userDetails, Long lastReviewId,
            Pageable pageable) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Slice<Review> sliceReviews = reviewRepository.findByUserOrderByIdDesc(user, lastReviewId,
                pageable);

        List<MyMealReviewResponse> myMealReviewResponses = sliceReviews.getContent().stream()
                .map(MyMealReviewResponse::from).toList();

        return SliceResponse.<MyMealReviewResponse>builder()
                .numberOfElements(sliceReviews.getNumberOfElements())
                .hasNext(sliceReviews.hasNext())
                .dataList(myMealReviewResponses)
                .build();
    }
}
