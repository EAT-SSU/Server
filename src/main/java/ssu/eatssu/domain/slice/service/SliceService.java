package ssu.eatssu.domain.slice.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.constants.MenuType;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.review.dto.ReviewDetail;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.user.dto.MyReviewDetail;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

@Service
@RequiredArgsConstructor
public class SliceService {

	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;
	private final MenuRepository menuRepository;
	private final MealRepository mealRepository;

	public SliceResponse<MyReviewDetail> findMyReviews(
		CustomUserDetails userDetails,
		Pageable pageable,
		Long lastReviewId) {

		User user = userRepository.findById(userDetails.getId())
								  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

		Slice<Review> sliceReviews = reviewRepository.findByUserOrderByIdDesc(user, lastReviewId,
			pageable);

		return convertToMyReviewDetail(sliceReviews);
	}

	public SliceResponse<ReviewDetail> findReviews(MenuType menuType,
		Long menuId,
		Long mealId,
		Pageable pageable,
		Long lastReviewId,
		CustomUserDetails userDetails) {
		Slice<Review> sliceReviews = null;
		if (menuType == MenuType.FIXED) {
			Menu menu = menuRepository.findById(menuId)
									  .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));
			sliceReviews = reviewRepository.findAllByMenuOrderByIdDesc(menu, lastReviewId,
				pageable);
		}

		if (menuType == MenuType.VARIABLE) {
			Meal meal = mealRepository.findById(mealId)
									  .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));
			sliceReviews = reviewRepository.findAllByMealOrderByIdDesc(meal, lastReviewId,
				pageable);
		}

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

	private SliceResponse<MyReviewDetail> convertToMyReviewDetail(Slice<Review> sliceReviews) {
		List<MyReviewDetail> myReviewDetails = Optional.of(sliceReviews.getContent())
													   .orElse(List.of())
													   .stream()
													   .filter(Objects::nonNull)
													   .map(MyReviewDetail::from)
													   .collect(Collectors.toList());

		return SliceResponse.<MyReviewDetail>builder()
							.numberOfElements(sliceReviews.getNumberOfElements())
							.hasNext(sliceReviews.hasNext())
							.dataList(myReviewDetails)
							.build();
	}
}

