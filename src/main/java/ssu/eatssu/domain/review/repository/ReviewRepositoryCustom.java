package ssu.eatssu.domain.review.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.user.entity.User;

public interface ReviewRepositoryCustom {

    //특정 메뉴 리뷰 - 최신순 정렬
    Slice<Review> findAllByMenuOrderByIdDesc(Menu menu, Long lastReviewId, Pageable pageable);

    //특정 메뉴 리뷰 - 오래된순 정렬
    //특정 식단 리뷰 - 최신순 정렬
    Slice<Review> findAllByMealOrderByIdDesc(Meal meal, Long lastReviewId, Pageable pageable);

    //특정 메뉴 리뷰 - 오래된순 정렬
    //특정 유저 리뷰 - 최신순 정렬
    Slice<Review> findByUserOrderByIdDesc(User user, Long lastReviewId, Pageable pageable);

    Slice<Review> findAllByMenuOrderByIdAsc(Menu menu, Long lastReviewId, Pageable pageable);

    Slice<Review> findAllByMealOrderByIdAsc(Meal meal, Long lastReviewId, Pageable pageable);


}
