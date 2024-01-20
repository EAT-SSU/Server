package ssu.eatssu.domain.admin.persistence;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ssu.eatssu.domain.admin.dto.BriefMenu;
import ssu.eatssu.domain.menu.entity.QMenu;
import ssu.eatssu.domain.restaurant.entity.QRestaurant;
import ssu.eatssu.domain.restaurant.entity.RestaurantName;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LoadFixMenuRepository {
    private final JPAQueryFactory queryFactory;
    private final QMenu menu = QMenu.menu;
    private final QRestaurant restaurant = QRestaurant.restaurant;

    public List<BriefMenu> findBriefMenusByRestaurantName(RestaurantName restaurantName) {
        return queryFactory
                .select(Projections.constructor(BriefMenu.class,
                        menu.id,
                        menu.name,
                        menu.price))
                .from(menu)
                .join(menu.restaurant, restaurant)
                .where(
                        restaurantNameEq(restaurantName)
                )
                .orderBy(menu.name.asc())
                .fetch();
    }

    public boolean existsMenu(String name, RestaurantName restaurantName) {
        return queryFactory
                .select(menu.id)
                .from(menu)
                .join(menu.restaurant, restaurant)
                .where(
                        menuNameEq(name),
                        restaurantNameEq(restaurantName)
                )
                .fetchFirst() != null;
    }

    private BooleanExpression menuNameEq(String name) {
        return menu.name.eq(name);
    }

    private BooleanExpression restaurantNameEq(RestaurantName name) {
        return restaurant.restaurantName.eq(name);
    }

}
