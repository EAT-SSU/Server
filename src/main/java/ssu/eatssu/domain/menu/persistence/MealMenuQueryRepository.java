package ssu.eatssu.domain.menu.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.menu.entity.QMeal;
import ssu.eatssu.domain.menu.entity.QMealMenu;
import ssu.eatssu.domain.menu.entity.QMenu;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MealMenuQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QMenu menu = QMenu.menu;

    private final QMeal meal = QMeal.meal;

    private final QMealMenu mealMenu = QMealMenu.mealMenu;

    public List<Long> getMenuIds(Long mealId) {
        return queryFactory
                .select(menu.id)
                .from(meal)
                .join(mealMenu).on(meal.id.eq(mealMenu.meal.id))
                .join(menu).on(mealMenu.menu.id.eq(menu.id))
                .where(meal.id.eq(mealId))
                .fetch();
    }
}
