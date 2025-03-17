package ssu.eatssu.domain.menu.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.menu.entity.QMenu;
import ssu.eatssu.domain.review.entity.QReview;

@Repository
@RequiredArgsConstructor
public class QuerydslMealRatingCalculator {

	private final MealMenuQueryRepository mealMenuQueryRepository;

	private final JPAQueryFactory queryFactory;
	private final QMenu menu = QMenu.menu;

	private final QReview review = QReview.review;

	public Double getMainRatingAverage(Long mealId) {
		List<Long> menuIds = mealMenuQueryRepository.getMenuIds(mealId);
		return queryFactory
			.select(review.ratings.mainRating.avg())
			.from(review)
			.join(review.menu, menu)
			.where(
				menuIdIn(menuIds)
			)
			.fetchOne();
	}

	public Double getTasteRatingAverage(Long mealId) {
		List<Long> menuIds = mealMenuQueryRepository.getMenuIds(mealId);
		return queryFactory
			.select(review.ratings.tasteRating.avg())
			.from(review)
			.join(review.menu, menu)
			.where(
				menuIdIn(menuIds)
			)
			.fetchOne();
	}

	public Double getAmountRatingAverage(Long mealId) {
		List<Long> menuIds = mealMenuQueryRepository.getMenuIds(mealId);
		return queryFactory
			.select(review.ratings.amountRating.avg())
			.from(review)
			.join(review.menu, menu)
			.where(
				menuIdIn(menuIds)
			)
			.fetchOne();
	}

	private BooleanExpression menuIdIn(List<Long> menuIds) {
		return menuIds != null && !menuIds.isEmpty() ? menu.id.in(menuIds) : null;
	}
}
