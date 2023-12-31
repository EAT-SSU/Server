package ssu.eatssu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.Review;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.repository.ReviewRepository;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.response.BaseException;
import ssu.eatssu.web.SliceDto;
import ssu.eatssu.web.mypage.dto.MyReviewDetail;
import ssu.eatssu.web.mypage.dto.MypageInfo;

import java.util.ArrayList;
import java.util.List;

import static ssu.eatssu.response.BaseResponseStatus.NOT_FOUND_USER;

@RequiredArgsConstructor
@Service
@Transactional
public class MyPageService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    /**
     * 유저가 작성한 리뷰 목록 조회
     */
    public SliceDto<MyReviewDetail> findMyReviewList(Long userId, Pageable pageable, Long lastReviewId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        //pageable에서 sort값 가져옴
        String sortBy = pageable.getSort().get().findFirst().orElseThrow().getProperty();

        //pageable에서 direction값 가져옴
        Sort.Direction direction = pageable.getSort().get().findFirst().orElseThrow().getDirection();

        Slice<Review> sliceReviewList;
        if (sortBy.equals("date")) { //리뷰 날짜가 기준
            if (direction.isDescending()) { //최신순 정렬
                sliceReviewList = reviewRepository.findByUserOrderByIdDesc(user, lastReviewId, pageable);
            } else { //TODO 오래된 순 정렬 필요할까 ?
                sliceReviewList = reviewRepository.findByUserOrderByIdDesc(user, lastReviewId, pageable);
            }
        } else { //TODO 최신 순 외에 정렬 기준 추가 ?
            sliceReviewList = reviewRepository.findByUserOrderByIdDesc(user, lastReviewId, pageable);
        }
        return sliceReviewToSliceMyReviewDetail(sliceReviewList);

    }

    /**
     * Slice<Review>를 Slice<MyReviewDetail>로 변환
     */
    private SliceDto<MyReviewDetail> sliceReviewToSliceMyReviewDetail(Slice<Review> sliceReviewList) {
        List<MyReviewDetail> myReviewDetailList = new ArrayList<>();
        for (Review review : sliceReviewList) {
            MyReviewDetail myReviewDetail = MyReviewDetail.from(review);
            myReviewDetailList.add(myReviewDetail);
        }
        return new SliceDto<>(sliceReviewList.getNumberOfElements(),
                sliceReviewList.hasNext(), myReviewDetailList);
    }

    /**
     * 마이페이지 정보(닉네임, OAuth 제공처) 조회
     */
    public MypageInfo findMyPageInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        return new MypageInfo(user.getNickname(), user.getProvider());
    }
}
