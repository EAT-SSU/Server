package ssu.eatssu.domain.slice.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.auth.entity.CustomUserDetails;
import ssu.eatssu.domain.user.dto.MyReviewDetail;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

@Service
@RequiredArgsConstructor
public class SliceService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

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

    private SliceResponse<MyReviewDetail> convertToMyReviewDetail(Slice<Review> sliceReviews) {
        List<MyReviewDetail> myReviewDetails = sliceReviews.getContent().stream()
            .map(MyReviewDetail::from)
            .collect(Collectors.toList());

        return SliceResponse.<MyReviewDetail>builder()
            .numberOfElements(sliceReviews.getNumberOfElements())
            .hasNext(sliceReviews.hasNext())
            .dataList(myReviewDetails)
            .build();
    }
}

