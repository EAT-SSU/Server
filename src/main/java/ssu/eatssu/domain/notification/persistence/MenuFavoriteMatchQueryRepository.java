package ssu.eatssu.domain.notification.persistence;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.favorite.entity.QMenuFavorite;
import ssu.eatssu.domain.menu.entity.QMeal;
import ssu.eatssu.domain.menu.entity.QMealMenu;
import ssu.eatssu.domain.menu.entity.QMenu;
import ssu.eatssu.domain.menu.entity.constants.TimePart;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuFavoriteMatchQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QMenuFavorite favorite = QMenuFavorite.menuFavorite;
    private final QMenu favoriteMenu = new QMenu("favoriteMenu");
    private final QMenu servedMenu = new QMenu("servedMenu");
    private final QMealMenu mealMenu = QMealMenu.mealMenu;
    private final QMeal meal = QMeal.meal;

    public List<MenuFavoriteMatch> findMatches(LocalDate date, Collection<TimePart> timeParts) {
        return queryFactory.select(Projections.constructor(MenuFavoriteMatch.class,
                                   favorite.user.id, favoriteMenu.name, meal.restaurant, meal.timePart))
                           .distinct()
                           .from(favorite)
                           .join(favorite.menu, favoriteMenu)
                           .join(servedMenu).on(servedMenu.name.eq(favoriteMenu.name),
                                                servedMenu.restaurant.eq(favoriteMenu.restaurant))
                           .join(mealMenu).on(mealMenu.menu.id.eq(servedMenu.id))
                           .join(meal).on(mealMenu.meal.id.eq(meal.id))
                           .where(meal.date.eq(Date.valueOf(date)),
                                  meal.timePart.in(timeParts),
                                  meal.restaurant.eq(favoriteMenu.restaurant))
                           .fetch();
    }
}
