package ssu.eatssu.domain.menu.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ssu.eatssu.domain.menu.entity.QMenu;
import ssu.eatssu.domain.review.entity.QReview;

@Repository
@RequiredArgsConstructor
public class QuerydslMenuRatingCountProvider {
    private final JPAQueryFactory queryFactory;
    private final QMenu menu = QMenu.menu;
    private final QReview review = QReview.review;

    public Integer getReviewCountByStar(Long menuId, Integer star) {
        return queryFactory
                .select(review.count())
                .from(review)
                .join(review.menu, menu)
                .where(
                        menuIdEq(menuId),
                        reviewStarIs(star)
                )
                .fetchOne().intValue();
    }
    private BooleanExpression reviewStarIs(Integer star) {
        return review.ratings.mainRating.eq(star);
    }

    private BooleanExpression menuIdEq(Long menuId) {
        return menu.id.eq(menuId);
    }
}
