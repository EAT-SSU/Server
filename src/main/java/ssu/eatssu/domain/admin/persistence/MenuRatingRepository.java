package ssu.eatssu.domain.admin.persistence;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ssu.eatssu.domain.menu.entity.QMenu;
import ssu.eatssu.domain.review.entity.QReview;

@Repository
@RequiredArgsConstructor
public class MenuRatingRepository {
    private final JPAQueryFactory queryFactory;
    private final QMenu menu = QMenu.menu;
    private final QReview review = QReview.review;

    public Double getMainRatingAverage(Long menuId) {
        return queryFactory
                .select(review.ratings.mainRating.avg())
                .from(review)
                .join(review.menu, menu)
                .where(
                        menuIdEq(menuId)
                )
                .fetchOne();
    }

    private BooleanExpression menuIdEq(Long menuId) {
        return menu.id.eq(menuId);
    }

}
