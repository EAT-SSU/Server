package ssu.eatssu.domain.review.repository;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import ssu.eatssu.domain.review.QReview;
import ssu.eatssu.domain.review.entity.Review;

import java.util.ArrayList;
import java.util.List;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.user.entity.User;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QReview review = QReview.review;

    @Override
    public Slice<Review> findAllByMenuOrderByIdDesc(Menu menu, Long lastReviewId, Pageable pageable) {
        List<Review> reviewList = queryFactory.selectFrom(review)
                .where(
                        nextFromLastReviewId(lastReviewId),
                        review.menu.eq(menu)
                )
                .orderBy(review.id.desc())
                .limit(pageable.getPageSize() + 1L)
                .fetch();
        return checkLastPage(pageable, reviewList);
    }

    @Override
    public Slice<Review> findAllByMenuOrderByIdAsc(Menu menu, Long lastReviewId, Pageable pageable) {
        List<Review> reviewList = queryFactory.selectFrom(review)
                .where(
                        beforeFromLastReviewId(lastReviewId),
                        review.menu.eq(menu)
                )
                .orderBy(review.id.asc())
                .limit(pageable.getPageSize() + 1L)
                .fetch();
        return checkLastPage(pageable, reviewList);
    }

    @Override
    public Slice<Review> findAllByMealOrderByIdDesc(Meal meal, Long lastReviewId, Pageable pageable) {
        List<Menu> menuList = new ArrayList<>();
        meal.getMealMenus().forEach(mealMenu -> menuList.add(mealMenu.getMenu()));
        List<Review> reviewList = queryFactory.selectFrom(review)
                .where(
                        nextFromLastReviewId(lastReviewId),
                        review.menu.in(menuList)
                )
                .orderBy(review.id.desc())
                .limit(pageable.getPageSize() + 1L)
                .fetch();
        return checkLastPage(pageable, reviewList);
    }

    @Override
    public Slice<Review> findAllByMealOrderByIdAsc(Meal meal, Long lastReviewId, Pageable pageable) {
        List<Menu> menuList = new ArrayList<>();
        meal.getMealMenus().forEach(mealMenu -> menuList.add(mealMenu.getMenu()));
        List<Review> reviewList = queryFactory.selectFrom(review)
                .where(
                        beforeFromLastReviewId(lastReviewId),
                        review.menu.in(menuList)
                )
                .orderBy(review.id.asc())
                .limit(pageable.getPageSize() + 1L)
                .fetch();
        return checkLastPage(pageable, reviewList);
    }

    @Override
    public Slice<Review> findByUserOrderByIdDesc(User user, Long lastReviewId, Pageable pageable) {
        List<Review> reviewList = queryFactory.selectFrom(review)
                .where(
                        nextFromLastReviewId(lastReviewId),
                        review.user.eq(user)
                )
                .orderBy(review.id.desc())
                .limit(pageable.getPageSize() + 1L)
                .fetch();
        return checkLastPage(pageable, reviewList);
    }

    private BooleanExpression nextFromLastReviewId(Long reviewId) {
        if (reviewId == null) {
            return null;
        }
        return review.id.lt(reviewId); //reviewId < lastReviewId
    }

    private BooleanExpression beforeFromLastReviewId(Long reviewId) {
        if (reviewId == null) {
            return null;
        }
        return review.id.gt(reviewId); //reviewId > lastReviewId
    }

    //무한 스크롤 방식 처리하는 메소드
    private Slice<Review> checkLastPage(Pageable pageable, List<Review> results) {

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }


}
