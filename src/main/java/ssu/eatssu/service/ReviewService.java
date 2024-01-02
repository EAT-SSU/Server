package ssu.eatssu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ssu.eatssu.domain.*;
import ssu.eatssu.domain.repository.*;
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.utils.S3Uploader;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.SliceDto;
import ssu.eatssu.web.review.dto.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 리뷰 작성
     */
    public void createReview(Long userId, Long menuId, ReviewCreate reviewCreate, List<MultipartFile> imgList)  {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new BaseException(NOT_FOUND_USER));
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(()-> new BaseException(NOT_FOUND_MENU));
        Review review = reviewCreate.toEntity(user, menu);
        reviewRepository.save(review);
        menu.addReview(reviewCreate.getMainRate(),reviewCreate.getTasteRate(), reviewCreate.getAmountRate());
        menuRepository.save(menu);

        if(imgList!=null&&!imgList.isEmpty()){
            for(MultipartFile img : imgList){
                addReviewImg(review, img);
            }
        }
    }

    /**
     * 리뷰 이미지 추가
     */
    public void addReviewImg(Review review, MultipartFile image) {
        if(!image.isEmpty()){
            try{
                String reviewImgUrl = s3Uploader.upload(image, "reviewImg");
                ReviewImg reviewImg = ReviewImg.builder().review(review).imageUrl(reviewImgUrl).build();
                reviewImgRepository.save(reviewImg);
            }catch (IOException e){
                throw new BaseException(FAIL_IMG_UPLOAD);
            }
        }
    }

    /**
     * 리뷰 문장 수정
     */
    public void updateReviewContent(Long userId, Long reviewId, ReviewUpdate reviewUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new BaseException(NOT_FOUND_USER));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new BaseException(NOT_FOUND_REVIEW));

        if(isWriterOrAdmin(review, user)){
            review.update(reviewUpdate.getContent(), reviewUpdate.getMainRate(), reviewUpdate.getAmountRate()
                    , reviewUpdate.getTasteRate());
            review.getMenu().updateReview();
        }else{
            throw new BaseException(REVIEW_PERMISSION_DENIED);
        }
    }

    /**
     * 리뷰 삭제
     */
    public void deleteReview(Long userId, Long reviewId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new BaseException(NOT_FOUND_USER));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new BaseException(NOT_FOUND_REVIEW));
        if(isWriterOrAdmin(review, user)){
            reviewRepository.delete(review);
            reviewRepository.flush();
            review.getMenu().deleteReview();
        }else{
            throw new BaseException(REVIEW_PERMISSION_DENIED);
        }
    }

    /**
     * 리뷰 작성자/관리자 인지 확인 //todo 관리자인지는 빼도 될듯
     */
    public boolean isWriterOrAdmin(Review review, User user){
        return review.getUser() == user;
    }

    /**
     * 고정메뉴 - 리뷰 정보 조회
     */
    public MenuReviewInfo findReviewInfoByMenuId(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_MENU));
        List<String> reviewMenuList = new ArrayList<>();
        reviewMenuList.add(menu.getName());

        return MenuReviewInfo.builder()
                .menuName(reviewMenuList).mainRate(menu.getMainRate()).tasteRate(menu.getTasteRate())
                .amountRate(menu.getAmountRate()).totalReviewCount(menu.getReviewCnt())
                .reviewRateCnt(MenuReviewInfo.ReviewRateCnt.fromMap(getReviewRateCnt(menu)))
                .build();
    }

    /**
     * 변동메뉴 - 리뷰 정보 조회
     */
    public MenuReviewInfo findReviewInfoByMealId(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_MEAL));
        List<Menu> menuList = new ArrayList<>();
        meal.getMealMenus().forEach(mealMenu -> menuList.add(mealMenu.getMenu()));
        List<String> reviewMenuList = new ArrayList<>();
        menuList.forEach(menu ->reviewMenuList.add(menu.getName()));
        List<Map<Integer, Long>> rateCntMapList =
                menuList.stream().map(this::getReviewRateCnt).toList();
        Map<Integer, Long> totalRateCntMap = rateCntMapList.stream().flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
        meal.caculateRate();
        return MenuReviewInfo.builder()
                .menuName(reviewMenuList).mainRate(meal.getMainRate()).tasteRate(meal.getTasteRate())
                .amountRate(meal.getAmountRate()).totalReviewCount(menuList.stream().mapToInt(Menu::getReviewCnt).sum())
                .reviewRateCnt(MenuReviewInfo.ReviewRateCnt.fromMap(totalRateCntMap))
                .build();
    }

    /**
     * 메뉴의 리뷰 평점별 개수 조회
     */
    public Map<Integer, Long> getReviewRateCnt(Menu menu) {
        List<Review> reviewList = menu.getReviews();
        long oneCnt = reviewList.stream().filter(r -> r.getMainRate() == 1).count();
        long twoCnt = reviewList.stream().filter(r -> r.getMainRate() == 2).count();
        long threeCnt = reviewList.stream().filter(r -> r.getMainRate() == 3).count();
        long fourCnt = reviewList.stream().filter(r -> r.getMainRate() == 4).count();
        long fiveCnt = reviewList.stream().filter(r -> r.getMainRate() == 5).count();
        Map<Integer, Long> reviewRateCntMap = new HashMap<>();
        reviewRateCntMap.put(1, oneCnt);
        reviewRateCntMap.put(2,twoCnt);
        reviewRateCntMap.put(3,threeCnt);
        reviewRateCntMap.put(4,fourCnt);
        reviewRateCntMap.put(5,fiveCnt);
        return reviewRateCntMap;
    }

    /**
     * Slice<Review>를 Slice<ReviewDetail>로 변환
     */
    private SliceDto<ReviewDetail> sliceReviewToSliceReviewDetail(Slice<Review> sliceReviewList){
        Long userId = SecurityUtil.getLoginUserId();
        List<ReviewDetail> reviewDetailList = new ArrayList<>();
        for(Review review : sliceReviewList){
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
                .orElseThrow(()->new BaseException(NOT_FOUND_MENU));
        //pageable에서 sort값 가져옴
        String sortBy = pageable.getSort().get().findFirst().orElseThrow().getProperty();
        //pageable에서 direction값 가져옴
        Sort.Direction direction = pageable.getSort().get().findFirst().orElseThrow().getDirection();
        Slice<Review> sliceReviewList;
        if (sortBy.equals("date")){ //리뷰 날짜가 기준
            if(direction.isDescending()){ //최신순 정렬
                sliceReviewList = reviewRepository.findAllByMenuOrderByIdDesc(menu, lastReviewId, pageable);
            }else{
                sliceReviewList = reviewRepository.findAllByMenuOrderByIdAsc(menu, lastReviewId, pageable);
            }
        }else{ //TODO 날짜 외에 정렬 기준 추가
            sliceReviewList = reviewRepository.findAllByMenuOrderByIdDesc(menu, lastReviewId, pageable);
        }
        return sliceReviewToSliceReviewDetail(sliceReviewList);
    }

    /**
     * 변동메뉴 - 리뷰 목록 조회
     */
    public SliceDto<ReviewDetail> findReviewListByMealId(Long mealId, Pageable pageable, Long lastReviewId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(()->new BaseException(NOT_FOUND_MEAL));
        //pageable에서 sort값 가져옴
        String sortBy = pageable.getSort().get().findFirst().orElseThrow().getProperty();
        //pageable에서 direction값 가져옴
        Sort.Direction direction = pageable.getSort().get().findFirst().orElseThrow().getDirection();
        Slice<Review> sliceReviewList;
        if (sortBy.equals("date")){ //리뷰 날짜가 기준
            if(direction.isDescending()){ //최신순 정렬
                sliceReviewList = reviewRepository.findAllByMealOrderByIdDesc(meal, lastReviewId, pageable);
            }else{
                sliceReviewList = reviewRepository.findAllByMealOrderByIdAsc(meal, lastReviewId, pageable);
            }
        }else{ //TODO 날짜 외에 정렬 기준 추가
            sliceReviewList = reviewRepository.findAllByMealOrderByIdDesc(meal, lastReviewId, pageable);
        }
        return sliceReviewToSliceReviewDetail(sliceReviewList);
    }
}
