package ssu.eatssu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ssu.eatssu.domain.Meal;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.domain.ReviewImg;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.repository.*;
import ssu.eatssu.domain.review.Review;
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.utils.RatesCalculator;
import ssu.eatssu.utils.S3Uploader;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.vo.AverageRates;
import ssu.eatssu.vo.RateCountMap;
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
    private final ReviewImgRepository reviewImgRepository;
    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;

    private final S3Uploader s3Uploader;
    private final RatesCalculator ratesCalculator;

    /**
     * 리뷰 작성
     */
    public void createReview(Long userId, Long menuId, ReviewCreate reviewCreate, List<MultipartFile> imgList) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

        Review review = reviewCreate.toEntity(user, menu);
        reviewRepository.save(review);

        if (imgList != null && !imgList.isEmpty()) {
            for (MultipartFile img : imgList) {
                addReviewImg(review, img);
            }
        }
    }

    /**
     * 리뷰 이미지 추가
     */
    public void addReviewImg(Review review, MultipartFile image) {
        if (!image.isEmpty()) {
            try {
                String reviewImgUrl = s3Uploader.upload(image, "reviewImg");
                ReviewImg reviewImg = ReviewImg.builder().review(review).imageUrl(reviewImgUrl).build();
                reviewImgRepository.save(reviewImg);
            } catch (IOException e) {
                throw new BaseException(FAIL_IMG_UPLOAD);
            }
        }
    }

    /**
     * 리뷰 문장 및 별점 수정
     */
    public void updateReviewContent(Long userId, Long reviewId, ReviewUpdate reviewUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_REVIEW));

        if (isWriterOrAdmin(review, user)) {
            review.update(reviewUpdate.getContent()
                    , reviewUpdate.getMainRate(), reviewUpdate.getAmountRate(), reviewUpdate.getTasteRate());
        } else {
            throw new BaseException(REVIEW_PERMISSION_DENIED);
        }
    }

    /**
     * 리뷰 삭제
     */
    public void deleteReview(Long userId, Long reviewId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_REVIEW));

        if (isWriterOrAdmin(review, user)) {
            reviewRepository.delete(review);
            reviewRepository.flush();
        } else {
            throw new BaseException(REVIEW_PERMISSION_DENIED);
        }
    }

    /**
     * 리뷰 작성자/관리자 인지 확인 //todo 관리자인지는 빼도 될듯
     */
    public boolean isWriterOrAdmin(Review review, User user) {
        return review.getUser() == user;
    }

    /**
     * 고정메뉴 - 리뷰 정보 조회
     */
    public MenuReviewInfo findReviewInfoByMenuId(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

        AverageRates averageRate = ratesCalculator.menuAverageRates(menu);

        RateCountMap rateCountMap = ratesCalculator.menuRateCountMap(menu);

        return MenuReviewInfo.of(menu, averageRate, rateCountMap);

    }

    /**
     * 변동메뉴 - 리뷰 정보 조회
     */
    public MealReviewInfo findReviewInfoByMealId(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_MEAL));

        int reviewCount = ratesCalculator.mealTotalReviewCount(meal);

        AverageRates averageRate = ratesCalculator.mealAverageRates(meal);

        RateCountMap rateCountMap = ratesCalculator.mealRateCountMap(meal);

        return MealReviewInfo.of(meal.getMenuNameList(), reviewCount, averageRate, rateCountMap);
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
    public SliceDto<ReviewDetail> findReviewListByMenuId(Long menuId, Pageable pageable, Long lastReviewId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));
        //pageable에서 sort값 가져옴
        String sortBy = pageable.getSort().get().findFirst().orElseThrow().getProperty();
        //pageable에서 direction값 가져옴
        Sort.Direction direction = pageable.getSort().get().findFirst().orElseThrow().getDirection();
        Slice<Review> sliceReviewList;
        if (sortBy.equals("date")) { //리뷰 날짜가 기준
            if (direction.isDescending()) { //최신순 정렬
                sliceReviewList = reviewRepository.findAllByMenuOrderByIdDesc(menu, lastReviewId, pageable);
            } else {
                sliceReviewList = reviewRepository.findAllByMenuOrderByIdAsc(menu, lastReviewId, pageable);
            }
        } else { //TODO 날짜 외에 정렬 기준 추가
            sliceReviewList = reviewRepository.findAllByMenuOrderByIdDesc(menu, lastReviewId, pageable);
        }
        return sliceReviewToSliceReviewDetail(sliceReviewList);
    }

    /**
     * 변동메뉴 - 리뷰 목록 조회
     */
    public SliceDto<ReviewDetail> findReviewListByMealId(Long mealId, Pageable pageable, Long lastReviewId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_MEAL));
        //pageable에서 sort값 가져옴
        String sortBy = pageable.getSort().get().findFirst().orElseThrow().getProperty();
        //pageable에서 direction값 가져옴
        Sort.Direction direction = pageable.getSort().get().findFirst().orElseThrow().getDirection();
        Slice<Review> sliceReviewList;
        if (sortBy.equals("date")) { //리뷰 날짜가 기준
            if (direction.isDescending()) { //최신순 정렬
                sliceReviewList = reviewRepository.findAllByMealOrderByIdDesc(meal, lastReviewId, pageable);
            } else {
                sliceReviewList = reviewRepository.findAllByMealOrderByIdAsc(meal, lastReviewId, pageable);
            }
        } else { //TODO 날짜 외에 정렬 기준 추가
            sliceReviewList = reviewRepository.findAllByMealOrderByIdDesc(meal, lastReviewId, pageable);
        }
        return sliceReviewToSliceReviewDetail(sliceReviewList);
    }
}
