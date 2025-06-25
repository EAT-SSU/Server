package ssu.eatssu.domain.admin.persistence;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ssu.eatssu.domain.admin.dto.BriefMenu;
import ssu.eatssu.domain.admin.dto.MealInfo;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.QMeal;
import ssu.eatssu.domain.menu.entity.QMealMenu;
import ssu.eatssu.domain.menu.entity.QMenu;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LoadMealRepository {
    private final JPAQueryFactory queryFactory;
    private final QMenu menu = QMenu.menu;
    private final QMeal meal = QMeal.meal;
    private final QMealMenu mealMenu = QMealMenu.mealMenu;

    public List<Long> existsMeal(MealInfo query) {
        return queryFactory.select(meal.id)
                           .from(meal)
                           .where(
                                   mealRestaurantEq(query.restaurant()),
                                   mealDateEq(query.date()),
                                   mealTimePartEq(query.timePart())
                                 )
                           .fetch();
    }

    public List<String> findMenuNamesByMealId(Long mealId) {
        return queryFactory
                .select(menu.name)
                .from(mealMenu)
                .join(mealMenu.meal, meal)
                .on(mealIdEq(mealId))
                .join(mealMenu.menu, menu)
                .orderBy(menu.name.asc())
                .fetch();
    }

    public List<Long> getAllMealIdsByInfo(MealInfo query) {
        return queryFactory.select(meal.id)
                           .from(meal)
                           .where(
                                   mealRestaurantEq(query.restaurant()),
                                   mealDateEq(query.date()),
                                   mealTimePartEq(query.timePart())
                                 )
                           .fetch();
    }

    public List<BriefMenu> findBriefMenusByMealId(Long mealId) {
        return queryFactory
                .select(Projections.constructor(BriefMenu.class,
                                                menu.id,
                                                menu.name,
                                                menu.price))
                .from(mealMenu)
                .join(mealMenu.meal, meal)
                .on(mealIdEq(mealId))
                .join(mealMenu.menu, menu)
                .orderBy(menu.name.asc())
                .fetch();
    }

    public Menu findMenu(String menuName, Restaurant restaurant) {
        return queryFactory.selectFrom(menu)
                           .where(
                                   menuNameEq(menuName),
                                   menuRestaurantEq(restaurant)
                                 )
                           .fetchFirst();
    }

    public List<Long> getAllMenuIds(Long mealId) {
        return queryFactory.select(mealMenu.menu.id)
                           .from(mealMenu)
                           .where(mealIdEq(mealId))
                           .fetch();
    }

    private BooleanExpression mealIdEq(Long mealId) {
        return meal.id.eq(mealId);
    }

    private BooleanExpression mealTimePartEq(TimePart timePart) {
        return meal.timePart.eq(timePart);
    }

    private BooleanExpression mealDateEq(Date date) {
        return meal.date.eq(date);
    }

    private BooleanExpression mealRestaurantEq(Restaurant restaurant) {
        return meal.restaurant.eq(restaurant);
    }

    private BooleanExpression menuRestaurantEq(Restaurant restaurant) {
        return menu.restaurant.eq(restaurant);
    }

    private BooleanExpression menuNameEq(String menuName) {
        return menu.name.eq(menuName);
    }

    public int countMealMenuByMenuId(Long menuId) {
        return (int) queryFactory.selectFrom(mealMenu)
                                 .where(mealMenu.menu.id.eq(menuId))
                                 .fetchCount();
    }
}
