package ssu.eatssu.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.repository.MealRepository;
import ssu.eatssu.domain.menu.repository.MenuRepository;
import ssu.eatssu.domain.rating.entity.RatingCalculator;
import ssu.eatssu.domain.review.dto.*;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewImage;
import ssu.eatssu.domain.review.repository.ReviewImageRepository;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.util.S3Uploader;

import java.io.IOException;
import java.util.List;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
@Transactional
public class ReviewService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;
    private final RatingCalculator ratingCalculator;
    private final S3Uploader s3Uploader;

    @Deprecated
    public Review createReview(CustomUserDetails userDetails, Long menuId, ReviewCreateRequest request,
                               List<MultipartFile> images) {

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

        Review review = request.toEntity(user, menu);

        menu.addReview(review);
        processReviewImages(images, review);

        return reviewRepository.save(review);
    }

    private void processReviewImages(List<MultipartFile> images, Review review) {
        if (images == null || images.isEmpty()) {
            return;
        }
        for (MultipartFile image : images) {
            addReviewImage(review, image);
        }
    }

    private void addReviewImage(Review review, MultipartFile image) {
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

    public SavedReviewImage uploadImage(MultipartFile image) {
        try {
            String imageUrl = s3Uploader.upload(image, "reviewImg");
            return new SavedReviewImage(imageUrl);
        } catch (IOException e) {
            throw new BaseException(FAIL_IMAGE_UPLOAD);
        }
    }

    public void uploadReview(CustomUserDetails userDetails, Long menuId, UploadReviewRequest request) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

        Review review = request.toReviewEntity(user, menu);
        reviewRepository.save(review);

        ReviewImage reviewImage = new ReviewImage(review, request.getImageUrl());
        reviewImageRepository.save(reviewImage);

        menu.addReview(review);
    }

    public void createReview(CustomUserDetails userDetails, Long mealId, CreateReviewRequest request) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_MEAL));

        Review review = request.toReviewEntity(user, meal);
        reviewRepository.save(review);

        List<ReviewImage> reviewImages = request.createReviewImages(review);
        reviewImageRepository.saveAll(reviewImages);

        for (MenuLikeRequest menuLikes : request.getMenuLikes()) {
            Menu menu = menuRepository.findById(menuLikes.getMenuId())
                    .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

            if (menuLikes.getLike()) {
                menu.getLike();
            }
        }
    }

    public void updateReview(CustomUserDetails userDetails, Long reviewId,
                             ReviewUpdateRequest request) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_REVIEW));

        if (review.isNotWrittenBy(user)) {
            throw new BaseException(REVIEW_PERMISSION_DENIED);
        }

        review.update(request.content(), request.mainRating(), request.amountRating(),
                request.tasteRating());
    }


    public void deleteReview(CustomUserDetails userDetails, Long reviewId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_REVIEW));

        if (review.isNotWrittenBy(user)) {
            throw new BaseException(REVIEW_PERMISSION_DENIED);
        }
        reviewRepository.delete(review);
        reviewRepository.flush();
    }

    public MenuReviewResponse findMenuReviews(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));

        RatingAverages ratingAverages = ratingCalculator.menuAverageRatings(menu);
        ReviewRatingCount ratingCount = ratingCalculator.menuRatingCount(menu);

        return MenuReviewResponse.of(menu, ratingAverages, ratingCount);

    }

    public MealReviewsResponse findMealReviews(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_MEAL));

        long reviewCount = ratingCalculator.mealTotalReviewCount(meal);

        RatingAverages averageRating = ratingCalculator.mealAverageRatings(meal);
        ReviewRatingCount ratingCountMap = ratingCalculator.mealRatingCount(meal);

        return MealReviewsResponse.of(reviewCount, meal.getMenuNames(), averageRating,
                ratingCountMap);
    }
}
