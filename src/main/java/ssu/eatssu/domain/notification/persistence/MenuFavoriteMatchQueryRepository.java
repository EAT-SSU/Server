package ssu.eatssu.domain.notification.persistence;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.favorite.entity.QMenuFavorite;
import ssu.eatssu.domain.menu.entity.QMeal;
import ssu.eatssu.domain.menu.entity.QMealMenu;
import ssu.eatssu.domain.menu.entity.QMenu;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        List<Tuple> rows = queryFactory.select(favorite.user.id, favoriteMenu.id, favoriteMenu.nameKey,
                                               favoriteMenu.name,
                                               favoriteMenu.nameEn, meal.restaurant, meal.timePart)
                                       .distinct()
                                       .from(favorite)
                                       .join(favorite.menu, favoriteMenu)
                                       .join(servedMenu).on(servedMenu.nameKey.eq(favoriteMenu.nameKey),
                                                            servedMenu.restaurant.eq(favoriteMenu.restaurant))
                                       .join(mealMenu).on(mealMenu.menu.id.eq(servedMenu.id))
                                       .join(meal).on(mealMenu.meal.id.eq(meal.id))
                                       .where(meal.date.eq(Date.valueOf(date)),
                                              meal.timePart.in(timeParts),
                                              meal.restaurant.eq(favoriteMenu.restaurant))
                                       .orderBy(favorite.user.id.asc(), favoriteMenu.id.asc())
                                       .fetch();

        Map<MatchKey, MenuFavoriteMatch> uniqueMatches = new LinkedHashMap<>();
        for (Tuple row : rows) {
            MatchKey key = new MatchKey(row.get(favorite.user.id), row.get(favoriteMenu.nameKey),
                                        row.get(meal.restaurant), row.get(meal.timePart));
            uniqueMatches.putIfAbsent(key, new MenuFavoriteMatch(key.userId(), row.get(favoriteMenu.name),
                                                                 row.get(favoriteMenu.nameEn), key.restaurant(),
                                                                 key.timePart()));
        }
        return List.copyOf(uniqueMatches.values());
    }

    private record MatchKey(Long userId, String nameKey, Restaurant restaurant, TimePart timePart) {
    }
}
