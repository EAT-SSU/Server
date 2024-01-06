package ssu.eatssu.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ssu.eatssu.domain.menu.Meal;
import ssu.eatssu.domain.menu.Menu;
import ssu.eatssu.domain.review.Review;
import ssu.eatssu.domain.user.User;

public interface ReviewRepositoryCustom {
    //특정 메뉴 리뷰 - 최신순 정렬
    Slice<Review> findAllByMenuOrderByIdDesc(Menu menu, Long lastReviewId, Pageable pageable);
    //특정 메뉴 리뷰 - 오래된순 정렬
    Slice<Review> findAllByMenuOrderByIdAsc(Menu menu, Long lastReviewId, Pageable pageable);
    //특정 식단 리뷰 - 최신순 정렬
    Slice<Review> findAllByMealOrderByIdDesc(Meal meal, Long lastReviewId, Pageable pageable);
    //특정 메뉴 리뷰 - 오래된순 정렬
    Slice<Review> findAllByMealOrderByIdAsc(Meal meal, Long lastReviewId, Pageable pageable);

    //특정 유저 리뷰 - 최신순 정렬
    Slice<Review> findByUserOrderByIdDesc(User user, Long lastReviewId, Pageable pageable);


}
