package ssu.eatssu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ssu.eatssu.domain.menu.Meal;
import ssu.eatssu.domain.menu.Menu;
import ssu.eatssu.domain.rate.RateCalculator;
import ssu.eatssu.domain.repository.*;
import ssu.eatssu.domain.review.Review;
import ssu.eatssu.domain.review.ReviewImage;
import ssu.eatssu.domain.user.User;
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.utils.S3Uploader;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.SliceDto;
import ssu.eatssu.web.review.dto.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ssu.eatssu.handler.response.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
@Transactional
public class ReviewService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;
    private final S3Uploader s3Uploader;
    private final RateCalculator rateCalculator;

    /**
     * 리뷰 작성
     */
    public void write(Long userId, Long menuId, CreateReviewRequest request,
        List<MultipartFile> images) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

        Review review = request.toEntity(user, menu);

        menu.addReview(review);
        processReviewImages(images, review);
        reviewRepository.save(review);
    }

    public void processReviewImages(List<MultipartFile> images, Review review) {
        if (images == null || !images.isEmpty()) {
            return;
        }
        for (MultipartFile image : images) {
            addReviewImage(review, image);
        }
    }

    /**
     * 리뷰 이미지 추가
     */
    public void addReviewImage(Review review, MultipartFile image) {
        if (!image.isEmpty()) {
            try {
	String reviewImageUrl = s3Uploader.upload(image, "reviewImg");
	ReviewImage reviewImage = ReviewImage.builder().review(review)
	    .imageUrl(reviewImageUrl)
	    .build();
	reviewImageRepository.save(reviewImage);
            } catch (IOException e) {
	throw new BaseException(FAIL_IMAGE_UPLOAD);
            }
        }
    }

    /**
     * 리뷰 문장 수정
     */
    public void update(Long userId, Long reviewId, UpdateReviewRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new BaseException(NOT_FOUND_REVIEW));

        if (review.isDifferentUser(user)) {
            throw new BaseException(REVIEW_PERMISSION_DENIED);
        }

        review.update(request);
    }

    /**
     * 리뷰 삭제
     */
    public void delete(Long userId, Long reviewId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new BaseException(NOT_FOUND_REVIEW));

        if (review.isDifferentUser(user)) {
            throw new BaseException(REVIEW_PERMISSION_DENIED);
        }
        reviewRepository.delete(review);
        reviewRepository.flush();
    }

    /**
     * 고정메뉴 - 리뷰 정보 조회
     */
    public MenuReviewInformationResponse findReviewInformationByMenuId(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

        AverageReviewRateResponse averageReviewRate = rateCalculator.menuAverageRate(menu);
        ReviewRateCountResponse reviewRateCount = rateCalculator.menuRateCount(menu);
        return MenuReviewInformationResponse.of(menu, averageReviewRate, reviewRateCount);
    }

    /**
     * 변동메뉴 - 리뷰 정보 조회
     */
    public MealReviewInformationResponse findReviewInformationByMealId(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
            .orElseThrow(() -> new BaseException(NOT_FOUND_MEAL));

        AverageReviewRateResponse averageReviewRate = rateCalculator.mealAverageRate(meal);
        ReviewRateCountResponse reviewRateCount = rateCalculator.mealRateCount(meal);

        return MealReviewInformationResponse.of(meal, meal.getMenuNameList(), averageReviewRate,
            reviewRateCount);
    }

    /**
     * Slice<Review>를 Slice<ReviewDetail>로 변환
     */
    private SliceDto<ReviewDetail> sliceReviewToSliceReviewDetail(Slice<Review> sliceReviewList) {
        Long userId = SecurityUtil.getLoginUserId();
        List<ReviewDetail> reviewDetailList = new ArrayList<>();
        for (Review review : sliceReviewList) {
            ReviewDetail reviewDetail = ReviewDetail.from(review, userId);
            reviewDetailList.add(reviewDetail);
        }
        return new SliceDto<>(sliceReviewList.getNumberOfElements(),
            sliceReviewList.hasNext(), reviewDetailList);
    }

    /**
     * 고정메뉴 - 리뷰 목록 조회
     */
    public SliceDto<ReviewDetail> findReviewListByMenuId(Long menuId, Pageable pageable,
        Long lastReviewId) {
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));
        //pageable에서 sort값 가져옴
        String sortBy = pageable.getSort().get().findFirst().orElseThrow().getProperty();
        //pageable에서 direction값 가져옴
        Sort.Direction direction = pageable.getSort().get().findFirst().orElseThrow()
            .getDirection();
        Slice<Review> sliceReviewList;
        if (sortBy.equals("date")) { //리뷰 날짜가 기준
            if (direction.isDescending()) { //최신순 정렬
	sliceReviewList = reviewRepository.findAllByMenuOrderByIdDesc(menu, lastReviewId,
	    pageable);
            } else {
	sliceReviewList = reviewRepository.findAllByMenuOrderByIdAsc(menu, lastReviewId,
	    pageable);
            }
        } else { //TODO 날짜 외에 정렬 기준 추가
            sliceReviewList = reviewRepository.findAllByMenuOrderByIdDesc(menu, lastReviewId,
	pageable);
        }
        return sliceReviewToSliceReviewDetail(sliceReviewList);
    }

    /**
     * 변동메뉴 - 리뷰 목록 조회
     */
    public SliceDto<ReviewDetail> findReviewListByMealId(Long mealId, Pageable pageable,
        Long lastReviewId) {
        Meal meal = mealRepository.findById(mealId)
            .orElseThrow(() -> new BaseException(NOT_FOUND_MEAL));
        //pageable에서 sort값 가져옴
        String sortBy = pageable.getSort().get().findFirst().orElseThrow().getProperty();
        //pageable에서 direction값 가져옴
        Sort.Direction direction = pageable.getSort().get().findFirst().orElseThrow()
            .getDirection();
        Slice<Review> sliceReviewList;
        if (sortBy.equals("date")) { //리뷰 날짜가 기준
            if (direction.isDescending()) { //최신순 정렬
	sliceReviewList = reviewRepository.findAllByMealOrderByIdDesc(meal, lastReviewId,
	    pageable);
            } else {
	sliceReviewList = reviewRepository.findAllByMealOrderByIdAsc(meal, lastReviewId,
	    pageable);
            }
        } else { //TODO 날짜 외에 정렬 기준 추가
            sliceReviewList = reviewRepository.findAllByMealOrderByIdDesc(meal, lastReviewId,
	pageable);
        }
        return sliceReviewToSliceReviewDetail(sliceReviewList);
    }
}
