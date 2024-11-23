package ssu.eatssu.domain.review.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_MEAL;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_MENU;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_REVIEW;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.REVIEW_PERMISSION_DENIED;

import java.util.LinkedHashMap;
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
import ssu.eatssu.domain.review.entity.ReviewImage;
import ssu.eatssu.domain.review.entity.ReviewMenuLike;
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

        if (menuIds.isEmpty()) {
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

        List<ReviewMenuLike> reviewMenuLikes = reviewMenuLikeRepository.findByReview(review);

        Map<Long, Boolean> menuLikes = request.getMenuLikes().stream()
                .collect(Collectors.toMap(MenuLikeRequest::getMenuId, MenuLikeRequest::getIsLike));

        // 기존 데이터 수정 또는 삭제
        for (ReviewMenuLike reviewMenuLike : reviewMenuLikes) {
            Boolean updatedIsLike = menuLikes.get(reviewMenuLike.getMenu().getId());
            if (updatedIsLike == null) {
                // 요청 데이터에 없으면 삭제
                reviewMenuLikeRepository.delete(reviewMenuLike);
                if (reviewMenuLike.getIsLike()) {
                    reviewMenuLike.getMenu().decreaseLikeCount();
                } else {
                    reviewMenuLike.getMenu().increaseUnlikeCount();
                }
            } else if (!reviewMenuLike.getIsLike().equals(updatedIsLike)) {
                // 요청 데이터와 다르면 수정
                reviewMenuLike.updateLike(updatedIsLike);
                if (updatedIsLike) {
                    reviewMenuLike.getMenu().increaseLikeCount();
                    reviewMenuLike.getMenu().decreaseUnlikeCount();
                } else {
                    reviewMenuLike.getMenu().increaseUnlikeCount();
                    reviewMenuLike.getMenu().decreaseLikeCount();
                }
            }
            menuLikes.remove(reviewMenuLike.getMenu().getId());
        }

        // 새롭게 리뷰 요청된 메뉴 데이터 처리
        for (Map.Entry<Long, Boolean> entry : menuLikes.entrySet()) {
            Menu menu = menuRepository.findById(entry.getKey())
                    .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

            ReviewMenuLike reviewMenuLike = ReviewMenuLike.builder()
                    .review(review)
                    .menu(menu)
                    .isLike(entry.getValue()).build();
            reviewMenuLikeRepository.save(reviewMenuLike);

            if (entry.getValue()) {
                menu.increaseLikeCount();
            } else {
                menu.increaseUnlikeCount();
            }
        }

        // 리뷰 본문 및 평점 수정
        review.setContent(request.getContent());
        review.setRating(request.getRating());
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

        List<ReviewMenuLike> reviewMenuLikes = reviewMenuLikeRepository.findByReview(review);

        // ReviewMenuLike 삭제 전, 메뉴의 likeCount와 unlikeCount 수정
        for (ReviewMenuLike reviewMenuLike : reviewMenuLikes) {
            Menu menu = reviewMenuLike.getMenu();
            if (reviewMenuLike.getIsLike()) {
                menu.decreaseLikeCount();
            } else {
                menu.decreaseUnlikeCount();
            }
        }

        reviewMenuLikeRepository.deleteAll(reviewMenuLikes);
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
