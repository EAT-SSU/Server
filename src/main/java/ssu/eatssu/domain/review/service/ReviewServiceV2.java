package ssu.eatssu.domain.review.service;

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
import ssu.eatssu.domain.review.dto.CreateMenuReviewRequest;
import ssu.eatssu.domain.review.dto.MealReviewResponse;
import ssu.eatssu.domain.review.dto.MealReviewsV2Response;
import ssu.eatssu.domain.review.dto.MenuLikeRequest;
import ssu.eatssu.domain.review.dto.MenuReviewsV2Response;
import ssu.eatssu.domain.review.dto.RestaurantReviewResponse;
import ssu.eatssu.domain.review.dto.ReviewDetail;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;
import ssu.eatssu.domain.review.dto.UpdateMealReviewRequest;
import ssu.eatssu.domain.review.dto.ValidMenuForViewResponse;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewImage;
import ssu.eatssu.domain.review.repository.ReviewImageRepository;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.review.utils.MenuFilterUtil;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.user.dto.MyMealReviewResponse;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_MEAL;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_MENU;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_REVIEW;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.REVIEW_PERMISSION_DENIED;

@RequiredArgsConstructor
@Service
public class ReviewServiceV2 {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;
    private final MealMenuRepository mealMenuRepository;
    private final ReviewImageRepository reviewImageRepository;

    /**
     * meal에 대한 리뷰 생성
     */
    @Transactional
    public void createMealReview(CustomUserDetails userDetails, CreateMealReviewRequest request) {
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
     * menu에 대한 리뷰 작성
     */
    @Transactional
    public void createMenuReview(CustomUserDetails userDetails, CreateMenuReviewRequest request) {
        User user = userRepository.findById(userDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Menu menu = menuRepository.findById(request.getMenuId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

        Review review = request.toReviewEntity(user, menu);
        review.addReviewMenuLike(menu, request.getMenuLike().getIsLike());
        reviewRepository.save(review);

        ReviewImage reviewImage = new ReviewImage(review, request.getImageUrl());
        reviewImageRepository.save(reviewImage);

        menu.addReview(review);
    }

    /**
     * 특정 식당 리뷰 조회
     */
    public RestaurantReviewResponse findRestaurantReviews(Restaurant restaurant) {
        List<Meal> meals = mealRepository.findByRestaurant(restaurant);
        List<Review> reviews = reviewRepository.findByMealIn(meals);
        List<Menu> menus = mealMenuRepository.findMenusByMeals(meals);

        Double averageRating = Optional.ofNullable(reviews)
                                       .orElse(Collections.emptyList())
                                       .stream()
                                       .map(r -> {
                                           Integer main = (r.getRatings() != null) ? r.getRatings()
                                                                                      .getMainRating() : null;
                                           return (main != null) ? main : r.getRating();
                                       })
                                       .filter(Objects::nonNull)
                                       .mapToInt(Integer::intValue)
                                       .average()
                                       .orElse(0.0);

        Integer likeCount = Optional.ofNullable(menus)
                                    .orElse(Collections.emptyList())
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .map(Menu::getLikeCount)
                                    .filter(Objects::nonNull)
                                    .mapToInt(Integer::intValue)
                                    .sum();


        Integer unlikeCount = Optional.ofNullable(menus)
                                      .orElse(Collections.emptyList())
                                      .stream()
                                      .filter(Objects::nonNull)
                                      .map(Menu::getUnlikeCount)
                                      .filter(Objects::nonNull)
                                      .mapToInt(Integer::intValue)
                                      .sum();

        ReviewRatingCount reviewRatingCount = ReviewRatingCount.from(reviews);

        return RestaurantReviewResponse.builder()
                                       .totalReviewCount(reviews.size())
                                       .reviewRatingCount(reviewRatingCount)
                                       .mainRating(Math.round(averageRating * 10) / 10.0)
                                       .likeCount(likeCount)
                                       .unlikeCount(unlikeCount)
                                       .build();
    }

    /**
     * 특정 식단 리뷰 리스트 조회
     */
    public SliceResponse<MealReviewResponse> findMealReviewList(Long mealId, Long lastReviewId, Pageable pageable,
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

        Long userId = (userDetails != null) ? userDetails.getId() : null;
        List<MealReviewResponse> mealReviewResponses =
                pageReviews.getContent()
                           .stream()
                           .map(review -> MealReviewResponse.from(review,
                                                                  userId))
                           .collect(Collectors.toList());

        return SliceResponse.<MealReviewResponse>builder()
                            .numberOfElements(pageReviews.getNumberOfElements())
                            .hasNext(pageReviews.hasNext())
                            .dataList(mealReviewResponses)
                            .build();
    }

    /**
     * 특정 메뉴 리뷰 리스트 조회
     */

    public SliceResponse<ReviewDetail> findMenuReviewList(Long menuId,
                                                          Pageable pageable,
                                                          Long lastReviewId,
                                                          CustomUserDetails userDetails) {
        Slice<Review> sliceReviews = null;
        Menu menu = menuRepository.findById(menuId)
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));
        sliceReviews = reviewRepository.findAllByMenuOrderByIdDesc(menu, lastReviewId,
                                                                   pageable);

        Long userId = (userDetails != null) ? userDetails.getId() : null;
        return convertToReviewDetail(sliceReviews, userId);
    }

    private SliceResponse<ReviewDetail> convertToReviewDetail(Slice<Review> sliceReviews,
                                                              Long userId) {
        List<ReviewDetail> reviewDetails = sliceReviews.getContent().stream()
                                                       .map(review -> ReviewDetail.from(review, userId))
                                                       .collect(Collectors.toList());

        return SliceResponse.<ReviewDetail>builder()
                            .numberOfElements(sliceReviews.getNumberOfElements())
                            .hasNext(sliceReviews.hasNext())
                            .dataList(reviewDetails)
                            .build();
    }


    /**
     * 특정 Menu 리뷰 조회
     */
    public MenuReviewsV2Response findMenuReviews(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new BaseException(NOT_FOUND_MENU));
        List<Review> reviews = reviewRepository.findAllByMenu(menu);

        double averageRating = Optional.ofNullable(reviews)
                                       .orElse(Collections.emptyList())
                                       .stream()
                                       .map(r -> {
                                           Integer main = (r.getRatings() != null) ? r.getRatings()
                                                                                      .getMainRating() : null;
                                           return (main != null) ? main : r.getRating();
                                       })
                                       .filter(Objects::nonNull)
                                       .mapToInt(Integer::intValue)
                                       .average()
                                       .orElse(0.0);

        Integer likeCount = menu.getLikeCount();

        ReviewRatingCount reviewRatingCount = ReviewRatingCount.from(reviews);


        return MenuReviewsV2Response
                .builder()
                .menuName(menu.getName())
                .totalReviewCount((long) reviews.size())
                .reviewRatingCount(reviewRatingCount)
                .mainRating(Math.round(averageRating * 10) / 10.0)
                .likeCount(likeCount != null ? likeCount : 0)
                .build();
    }

    /**
     * 특정 Meal 리뷰 조회
     */
    public MealReviewsV2Response findMealReviews(Long mealId) {
        Meal meal = mealRepository.findById(mealId).orElseThrow(() -> new BaseException(NOT_FOUND_MEAL));
        List<Review> reviews = reviewRepository.findAllByMeal(meal);
        List<Menu> menus = mealMenuRepository.findMenusByMeal(meal);

        List<ValidMenuForViewResponse.MenuDto> validMenus = menus.stream()
                                                                 .filter(menu -> !MenuFilterUtil.isExcludedFromReview(
                                                                         menu.getName()))
                                                                 .map(menu -> ValidMenuForViewResponse.MenuDto.builder()
                                                                                                              .menuId(menu.getId())
                                                                                                              .name(menu.getName())
                                                                                                              .build())
                                                                 .toList();

        Double averageRating = Optional.ofNullable(reviews)
                                       .orElse(Collections.emptyList())
                                       .stream()
                                       .map(r -> {
                                           Integer main = (r.getRatings() != null) ? r.getRatings()
                                                                                      .getMainRating() : null;
                                           return (main != null) ? main : r.getRating();
                                       })
                                       .filter(Objects::nonNull)
                                       .mapToInt(Integer::intValue)
                                       .average()
                                       .orElse(0.0);

        Integer likeCount = Optional.ofNullable(menus)
                                    .orElse(Collections.emptyList())
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .map(Menu::getLikeCount)
                                    .filter(Objects::nonNull)
                                    .mapToInt(Integer::intValue)
                                    .sum();


        ReviewRatingCount reviewRatingCount = ReviewRatingCount.from(reviews);

        return MealReviewsV2Response
                .builder()
                .menuNames(validMenus.stream()
                                .filter(Objects::nonNull)
                                .map(ValidMenuForViewResponse.MenuDto::getName)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()))
                .totalReviewCount((long) reviews.size())
                .reviewRatingCount(reviewRatingCount)
                .mainRating(Math.round(averageRating * 10) / 10.0)
                .likeCount(likeCount)
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
                                              .collect(Collectors.toMap(
                                                      menuLike -> menuRepository.findById(menuLike.getMenuId())
                                                                                .orElseThrow(() -> new BaseException(
                                                                                        NOT_FOUND_MENU)),
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


    public ValidMenuForViewResponse validMenuForReview(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_MEAL));

        List<Menu> menus = mealMenuRepository.findMenusByMeal(meal);

        List<ValidMenuForViewResponse.MenuDto> validMenus = menus.stream()
                                                                 .filter(menu -> !MenuFilterUtil.isExcludedFromReview(
                                                                         menu.getName()))
                                                                 .map(menu -> ValidMenuForViewResponse.MenuDto.builder()
                                                                                                              .menuId(menu.getId())
                                                                                                              .name(menu.getName())
                                                                                                              .build())
                                                                 .collect(Collectors.toList());

        return ValidMenuForViewResponse.builder()
                                       .menuList(validMenus)
                                       .build();
    }
}
