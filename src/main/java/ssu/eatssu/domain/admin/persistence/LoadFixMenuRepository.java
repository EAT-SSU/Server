package ssu.eatssu.domain.admin.persistence;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ssu.eatssu.domain.admin.dto.BriefMenu;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.menu.entity.QMenu;
import ssu.eatssu.domain.menu.entity.QMenuCategory;
import ssu.eatssu.domain.restaurant.entity.QRestaurant;
import ssu.eatssu.domain.restaurant.entity.RestaurantName;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LoadFixMenuRepository {
    private final JPAQueryFactory queryFactory;
    private final QMenu menu = QMenu.menu;
    private final QRestaurant restaurant = QRestaurant.restaurant;
    private final QMenuCategory category = QMenuCategory.menuCategory;

    public List<BriefMenu> findBriefMenusByCategoryId(Long categoryId) {
        return queryFactory
                .select(Projections.constructor(BriefMenu.class,
                        menu.id,
                        menu.name,
                        menu.price))
                .from(menu)
                .join(menu.category, category)
                .where(
                        menuCategoryIdEq(categoryId)
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

    /**
     * 오버 로딩
     */
    public boolean existsMenu(String name, Long restaurantId) {
        return queryFactory
                .select(menu.name)
                .from(menu)
                .join(menu.restaurant, restaurant)
                .where(
                        menuNameEq(name),
                        restaurantIdEq(restaurantId)
                )
                .fetchFirst() != null;
    }

    public long getRestaurantId(Long menuId) {
        return queryFactory
                .select(menu.restaurant.id)
                .from(menu)
                .where(
                        menu.id.eq(menuId)
                )
                .fetchFirst();
    }

    public List<MenuCategory> findMenuCategoriesByRestaurant(RestaurantName restaurant) {
        return queryFactory
                .selectFrom(category)
                .where(
                        categoryRestaurantNameEq(restaurant)
                ).fetch();
    }

    private BooleanExpression menuNameEq(String MenuName) {
        return menu.name.eq(MenuName);
    }

    private BooleanExpression restaurantNameEq(RestaurantName restaurantName) {
        return restaurant.restaurantName.eq(restaurantName);
    }

    private BooleanExpression restaurantIdEq(Long restaurantId) {
        return restaurant.id.eq(restaurantId);
    }

    private BooleanExpression categoryRestaurantNameEq(RestaurantName restaurantName) {
        return category.restaurantName.eq(restaurantName);
    }

    private BooleanExpression menuCategoryIdEq(Long categoryId) {
        return category.id.eq(categoryId);
    }
}
