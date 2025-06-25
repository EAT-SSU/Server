package ssu.eatssu.domain.review.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.persistence.MealMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.dto.CreateMealReviewRequest;
import ssu.eatssu.domain.review.dto.MealReviewResponse;
import ssu.eatssu.domain.review.dto.MealReviewsV2Response;
import ssu.eatssu.domain.review.dto.MenuLikeRequest;
import ssu.eatssu.domain.review.dto.MenuReviewsV2Response;
import ssu.eatssu.domain.review.dto.RestaurantReviewResponse;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;
import ssu.eatssu.domain.review.dto.UpdateMealReviewRequest;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewLike;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.user.dto.MyMealReviewResponse;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

@RequiredArgsConstructor
@Service
public class ReviewServiceV2 {
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

		Double averageRating = Optional.ofNullable(reviews)
			.orElse(Collections.emptyList())
			.stream()
			.filter(Objects::nonNull)
			.map(Review::getRating)
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
			pageReviews.getContent().stream().map(review -> MealReviewResponse.from(review,
				userId)).collect(Collectors.toList());

		return SliceResponse.<MealReviewResponse>builder()
							.numberOfElements(pageReviews.getNumberOfElements())
							.hasNext(pageReviews.hasNext())
							.dataList(mealReviewResponses)
							.build();
	}



	/**
	 * 특정 Menu 리뷰 조회
	 */
	public MenuReviewsV2Response findMenuReviews(Long menuId) {
		Menu menu = menuRepository.findById(menuId).orElseThrow(()-> new BaseException(NOT_FOUND_MENU));
		List<Review> reviews = reviewRepository.findAllByMenu(menu);

		double averageRating = Optional.ofNullable(reviews)
									   .orElse(Collections.emptyList())
									   .stream()
									   .filter(Objects::nonNull)
									   .map(Review::getRating)
									   .filter(Objects::nonNull)
									   .mapToInt(Integer::intValue)
									   .average()
									   .orElse(0.0);

		Integer likeCount = menu.getLikeCount();
		Integer unlikeCount = menu.getUnlikeCount();

		ReviewRatingCount reviewRatingCount = ReviewRatingCount.from(reviews);
		System.out.println(reviewRatingCount);

		return MenuReviewsV2Response
				.builder()
				.menuName(menu.getName())
				.totalReviewCount((long)reviews.size())
				.reviewRatingCount(reviewRatingCount)
				.mainRating(Math.round(averageRating * 10) / 10.0)
				.likeCount(likeCount!=null?likeCount:0)
				.unlikeCount(unlikeCount!=null?unlikeCount:0)
				.build();
	}

	/**
	 * 특정 Meal 리뷰 조회
	 */
	public MealReviewsV2Response findMealReviews(Long mealId) {
		Meal meal = mealRepository.findById(mealId).orElseThrow(()-> new BaseException(NOT_FOUND_MEAL));
		List<Review> reviews = reviewRepository.findAllByMeal(meal);
		List<Menu> menus = mealMenuRepository.findMenusByMeal(meal);

		Double averageRating = Optional.ofNullable(reviews)
			.orElse(Collections.emptyList())
			.stream()
			.filter(Objects::nonNull)
			.map(Review::getRating)
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

		return MealReviewsV2Response
				.builder()
				.menuNames( menus.stream()
								 .filter(Objects::nonNull)
								 .map(Menu::getName)
								 .filter(Objects::nonNull)
								 .collect(Collectors.toList()))
				.totalReviewCount((long)reviews.size())
				.reviewRatingCount(reviewRatingCount)
				.mainRating(Math.round(averageRating * 10) / 10.0)
				.likeCount(likeCount)
				.unlikeCount(unlikeCount)
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


}
