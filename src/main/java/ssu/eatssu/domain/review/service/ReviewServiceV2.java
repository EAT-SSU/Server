package ssu.eatssu.domain.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
import ssu.eatssu.domain.menu.service.MealRatingService;
import ssu.eatssu.domain.rating.entity.RatingCalculator;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.dto.CreateMealReviewRequest;
import ssu.eatssu.domain.review.dto.CreateMenuReviewRequestV2;
import ssu.eatssu.domain.review.dto.MealReviewResponse;
import ssu.eatssu.domain.review.dto.MealReviewsV2Response;
import ssu.eatssu.domain.review.dto.MenuIdNameDto;
import ssu.eatssu.domain.review.dto.MenuLikeRequest;
import ssu.eatssu.domain.review.dto.MenuReviewsV2Response;
import ssu.eatssu.domain.review.dto.RatingAverages;
import ssu.eatssu.domain.review.dto.RestaurantReviewResponse;
import ssu.eatssu.domain.review.dto.ReviewDetail;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;
import ssu.eatssu.domain.review.dto.UpdateMealReviewRequest;
import ssu.eatssu.domain.review.dto.ValidMenuForViewResponse;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewImageRepository;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.review.utils.MenuFilterUtil;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.user.dto.MyMealReviewResponse;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.log.event.LogEvent;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceV2 {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;
    private final MealMenuRepository mealMenuRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RatingCalculator ratingCalculator;
    private final MealRatingService mealRatingService;

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

        eventPublisher.publishEvent(LogEvent.of(
                String.format("MealReview created: reviewId=%d, mealId=%d, userId=%d, images=%d, menuLikes=%d",
                        review.getId(),
                        meal.getId(),
                        user.getId(),
                        request.getImageUrls().size(),
                        request.getMenuLikes().size())
        ));
    }

    /**
     * menu에 대한 리뷰 작성
     */
    @Transactional
    public void createMenuReview(CustomUserDetails userDetails, CreateMenuReviewRequestV2 request) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Menu menu = menuRepository.findById(request.getMenuLike().getMenuId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

        Review review = request.toReviewEntity(user, menu);
        review.addReviewMenuLike(menu, request.getMenuLike().getIsLike());
        request.getImageUrls().forEach(review::addReviewImage);
        reviewRepository.save(review);


        menu.addReview(review);

        eventPublisher.publishEvent(LogEvent.of(
                String.format("MenuReview created: reviewId=%d, menuId=%d, userId=%d, isLike=%s, imageUrl=%s",
                              review.getId(),
                              menu.getId(),
                              user.getId(),
                              request.getMenuLike().getIsLike(),
                              request.getImageUrls().size())
        ));
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
                                       .rating(Math.round(averageRating * 10) / 10.0)
                                       .likeCount(likeCount)
                                       .unlikeCount(unlikeCount)
                                       .build();
    }

    /**
     * 특정 식단 리뷰 리스트 조회
     */
    public SliceResponse<MealReviewResponse> findMealReviewList(Long mealId, Long lastReviewId, Pageable pageable,
                                                                CustomUserDetails userDetails) {

        Meal meal = mealRepository.findById(mealId).orElseThrow(() -> new BaseException(NOT_FOUND_MEAL));

        List<Menu> menus = mealMenuRepository.findMenusByMeal(meal);
        if (menus.isEmpty()) {
            log.warn("No menus found for mealId={}", mealId);
        }

        List<ValidMenuForViewResponse.MenuDto> validMenus = menus.stream()
                                                                 .filter(menu -> !MenuFilterUtil.isExcludedFromReview(
                                                                         menu.getName()))
                                                                 .map(menu -> ValidMenuForViewResponse.MenuDto.builder()
                                                                                                              .menuId(menu.getId())
                                                                                                              .name(menu.getName())
                                                                                                              .build())
                                                                 .collect(Collectors.toList());


        if (validMenus.isEmpty()) {
            log.warn("No valid menus found for mealId={}", mealId);
            return SliceResponse.empty();
        }

        List<Long> validMenuIds = validMenus.stream().map(ValidMenuForViewResponse.MenuDto::getMenuId).toList();
        List<Long> mealIds = mealMenuRepository.findMealIdsByMenuIds(validMenuIds);
        if (mealIds.isEmpty()) {
            log.warn("No related mealIds found for validMenuIds={} in mealId={}", validMenuIds, mealId);
            return SliceResponse.empty();
        }

        Page<Review> pageReviews = reviewRepository.findReviewsByMealIds(mealIds, lastReviewId, pageable);

        Long userId = (userDetails != null) ? userDetails.getId() : null;

        List<MealReviewResponse> mealReviewResponses =
                pageReviews.getContent()
                           .stream()
                           .map(review -> MealReviewResponse.from(review,
                               userId, validMenus, review.getRating()))
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

        if (!reviews.isEmpty() && averageRating == 0.0) {
            log.warn("All reviews for menuId={} have null/invalid ratings", menuId);
        }

        Integer likeCount = menu.getLikeCount();

        ReviewRatingCount reviewRatingCount = ReviewRatingCount.from(reviews);


        return MenuReviewsV2Response
                .builder()
                .menuName(menu.getName())
                .totalReviewCount((long) reviews.size())
                .reviewRatingCount(reviewRatingCount)
                .rating(Math.round(averageRating * 10) / 10.0)
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
        if (menus.isEmpty()) {
            log.warn("No menus found for mealId={}", meal.getId());
        }

        List<ValidMenuForViewResponse.MenuDto> validMenus = menus.stream()
                                                                 .filter(menu -> !MenuFilterUtil.isExcludedFromReview(
                                                                         menu.getName()))
                                                                 .map(menu -> ValidMenuForViewResponse.MenuDto.builder()
                                                                                                              .menuId(menu.getId())
                                                                                                              .name(menu.getName())
                                                                                                              .build())
                                                                 .toList();

        if (validMenus.isEmpty()) {
            log.warn("No valid menus for review found in mealId={}", mealId);
        }

        long reviewCount = ratingCalculator.mealTotalReviewCount(meal);

        RatingAverages averageRating = ratingCalculator.mealAverageRatings(meal);
        ReviewRatingCount ratingCountMap = ratingCalculator.mealRatingCount(meal);

        if (!reviews.isEmpty() && averageRating.mainRating() == 0.0) {
            log.warn("All reviews have null/invalid ratings for mealId={}", mealId);
        }

        Integer likeCount = Optional.of(menus)
                                    .orElse(Collections.emptyList())
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .map(Menu::getLikeCount)
                                    .filter(Objects::nonNull)
                                    .mapToInt(Integer::intValue)
                                    .sum();


        return MealReviewsV2Response
                .builder()
                .menuList(validMenus.stream()
                                    .filter(Objects::nonNull)
                                    .map(menu -> new MenuIdNameDto(
                                            menu.getMenuId(),
                                            menu.getName()
                                    ))
                                    .collect(Collectors.toList()))
                .totalReviewCount(reviewCount)
                .reviewRatingCount(ratingCountMap)
                .rating(averageRating.mainRating())
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

        eventPublisher.publishEvent(LogEvent.of(
                String.format("Review updated: reviewId=%d, userId=%d, newRating=%d",
                        review.getId(), user.getId(), request.getRating())
        ));
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

        eventPublisher.publishEvent(LogEvent.of(
                String.format("Review deleted: reviewId=%d, userId=%d", review.getId(), user.getId())
        ));

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
