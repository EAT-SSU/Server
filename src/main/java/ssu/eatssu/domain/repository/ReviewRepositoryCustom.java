package ssu.eatssu.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.domain.Review;
import ssu.eatssu.domain.User;

public interface ReviewRepositoryCustom {
    //특정 메뉴 리뷰 - 최신순 정렬
    Slice<Review> findByMenuOrderByIdDesc(Menu menu, Long lastReviewId, Pageable pageable);
    //특정 메뉴 리뷰 - 오래된순 정렬
    Slice<Review> findByMenuOrderByIdAsc(Menu menu, Long lastReviewId, Pageable pageable);

    //특정 유저 리뷰 - 최신순 정렬
    Slice<Review> findByUserOrderByIdDesc(User user, Long lastReviewId, Pageable pageable);


}
