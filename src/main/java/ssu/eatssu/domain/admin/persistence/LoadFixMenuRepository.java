package ssu.eatssu.domain.admin.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.admin.dto.BriefMenu;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.menu.entity.QMenu;
import ssu.eatssu.domain.menu.entity.QMenuCategory;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

@Repository
@RequiredArgsConstructor
public class LoadFixMenuRepository {
	private final JPAQueryFactory queryFactory;
	private final QMenu menu = QMenu.menu;
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

	public boolean existsMenu(String name, Restaurant restaurant) {
		return queryFactory
			.select(menu.id)
			.from(menu)
			.where(
				menuNameEq(name),
				restaurantNameEq(restaurant)
			)
			.fetchFirst() != null;
	}

	public Restaurant getRestaurant(Long menuId) {
		return queryFactory
			.select(menu.restaurant)
			.from(menu)
			.where(
				menu.id.eq(menuId)
			)
			.fetchFirst();
	}

	public List<MenuCategory> findMenuCategoriesByRestaurant(Restaurant restaurant) {
		return queryFactory
			.selectFrom(category)
			.where(
				categoryRestaurantNameEq(restaurant)
			).fetch();
	}

	private BooleanExpression menuNameEq(String menuName) {
		return menu.name.eq(menuName);
	}

	private BooleanExpression restaurantNameEq(Restaurant restaurant) {
		return menu.restaurant.eq(restaurant);
	}

	private BooleanExpression categoryRestaurantNameEq(Restaurant restaurant) {
		return category.restaurant.eq(restaurant);
	}

	private BooleanExpression menuCategoryIdEq(Long categoryId) {
		return category.id.eq(categoryId);
	}

}
