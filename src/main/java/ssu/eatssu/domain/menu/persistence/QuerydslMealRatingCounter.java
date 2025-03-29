package ssu.eatssu.domain.menu.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.menu.entity.QMenu;
import ssu.eatssu.domain.review.entity.QReview;

@Repository
@RequiredArgsConstructor
public class QuerydslMealRatingCounter {

	private final MealMenuQueryRepository mealMenuQueryRepository;

	private final JPAQueryFactory queryFactory;
	private final QMenu menu = QMenu.menu;

	private final QReview review = QReview.review;

	public Map<Integer, Long> getRatingCountMap(Long mealId) {
		List<Tuple> tuples = queryFactory.select(review.ratings.mainRating, review.count())
										 .from(review)
										 .join(review.menu, menu)
										 .groupBy(review.ratings.mainRating)
										 .where(menuIdIn(mealMenuQueryRepository.getMenuIds(mealId)))
										 .fetch();

		Map<Integer, Long> ratingCountMap = new HashMap<>();
		for (Tuple tuple : tuples) {
			Integer rating = tuple.get(review.ratings.mainRating);
			Long count = tuple.get(review.count());
			ratingCountMap.put(rating, count);
		}
		return ratingCountMap;
	}

	public Long getTotalRatingCount(Long mealId) {
		return queryFactory
			.select(review.count())
			.from(review)
			.join(review.menu, menu)
			.where(menuIdIn(mealMenuQueryRepository.getMenuIds(mealId)))
			.fetchOne();
	}

	private BooleanExpression menuIdIn(List<Long> menuIds) {
		return menuIds != null && !menuIds.isEmpty() ? menu.id.in(menuIds) : null;
	}
}

