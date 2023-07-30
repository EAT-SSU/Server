package ssu.eatssu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.domain.Review;
import ssu.eatssu.domain.ReviewImg;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.repository.MenuRepository;
import ssu.eatssu.domain.repository.ReviewImgRepository;
import ssu.eatssu.domain.repository.ReviewRepository;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.utils.S3Uploader;
import ssu.eatssu.web.SliceDto;
import ssu.eatssu.web.review.dto.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ReviewService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final MenuRepository menuRepository;
    private final S3Uploader s3Uploader;

    public void createReview(Long userId, Long menuId, ReviewCreate reviewCreate, List<MultipartFile> imgList) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("Not found user"));
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Not found menu"));
        Review review = reviewCreate.toEntity(user, menu);
        reviewRepository.save(review);
        menu.addReview(reviewCreate.getGrade());
        menuRepository.save(menu);

        for(MultipartFile img : imgList){
            addReviewImg(review, img);
        }
    }
    public void addReviewImg(Review review, MultipartFile image) throws IOException {
        if(!image.isEmpty()){
            String reviewImgUrl = s3Uploader.upload(image, "reviewImg");
            ReviewImg reviewImg = ReviewImg.builder().review(review).imageUrl(reviewImgUrl).build();
            reviewImgRepository.save(reviewImg);
        }
    }
    public void updateReview(Long userId, Long reviewId, ReviewUpdate reviewUpdate) throws IllegalAccessException {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("Not found user"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new IllegalArgumentException("Not found review"));

        if(isWriterOrAdmin(review, user)){
            review.update(reviewUpdate.getContent(), reviewUpdate.getGrade(), reviewUpdate.getReviewTags());
        }else{
            throw new IllegalAccessException("권한이 없는 유저입니다.");
        };

    }
    public void deleteReview(Long userId, Long reviewId) throws IllegalAccessException {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("Not found user"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new IllegalArgumentException("Not found review"));
        if(isWriterOrAdmin(review, user)){
            reviewRepository.delete(review);
        }else{
            throw new IllegalAccessException("권한이 없는 유저입니다.");
        };
    }
    public boolean isWriterOrAdmin(Review review, User user){
        if(review.getUser()==user){
            return true;
        }else{
            return false;
        }
    }
    public MenuReviewInfo findMenuReviewInfo(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
        return MenuReviewInfo.builder()
                .menuName(menu.getName()).grade(menu.getGrade()).totalReviewCount(menu.getReviewCnt())
                .reviewGradeCnt(findMenuReviewGradeCnt(menu))
                .build();
    }
    public MenuReviewInfo.ReviewGradeCnt findMenuReviewGradeCnt(Menu menu) {
        List<Review> reviewList = menu.getReviews();
        long oneCnt = reviewList.stream().filter(r -> r.getGrade() == 1).count();
        long twoCnt = reviewList.stream().filter(r -> r.getGrade() == 2).count();
        long threeCnt = reviewList.stream().filter(r -> r.getGrade() == 3).count();
        long fourCnt = reviewList.stream().filter(r -> r.getGrade() == 4).count();
        long fiveCnt = reviewList.stream().filter(r -> r.getGrade() == 5).count();

        return MenuReviewInfo.ReviewGradeCnt.builder()
                .oneCnt(oneCnt).twoCnt(twoCnt).threeCnt(threeCnt).fourCnt(fourCnt).fiveCnt(fiveCnt)
                .build();
    }
    public SliceDto<ReviewDetail> findReviewListByMenu(Long menuId, Pageable pageable, Long lastReviewId) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(()->new IllegalArgumentException("Menu not found"));
        //pageable에서 sort값 가져옴
        String sortBy = pageable.getSort().get().findFirst().orElseThrow().getProperty();
        //pageable에서 direction값 가져옴
        Sort.Direction direction = pageable.getSort().get().findFirst().orElseThrow().getDirection();
        Slice<Review> sliceReviewList;
        if (sortBy.equals("date")){ //리뷰 날짜가 기준
            if(direction.isDescending()){ //최신순 정렬
                sliceReviewList = reviewRepository.findByMenuOrderByIdDesc(menu, lastReviewId, pageable);
            }else{
                sliceReviewList = reviewRepository.findByMenuOrderByIdAsc(menu, lastReviewId, pageable);
            }
        }else{ //TODO 날짜 외에 정렬 기준 추가
            sliceReviewList = reviewRepository.findByMenuOrderByIdDesc(menu, lastReviewId, pageable);
        }
        return sliceReviewToSliceReviewDetail(sliceReviewList);

    }
    private SliceDto<ReviewDetail> sliceReviewToSliceReviewDetail(Slice<Review> sliceReviewList){
        List<ReviewDetail> reviewDetailList = new ArrayList<>();
        for(Review review : sliceReviewList){
            ReviewDetail reviewDetail = ReviewDetail.from(review);
            reviewDetailList.add(reviewDetail);
        }
        return new SliceDto<>(sliceReviewList.getNumberOfElements(),
                sliceReviewList.hasNext(), reviewDetailList);
    }

}
