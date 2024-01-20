package ssu.eatssu.domain.admin.persistence;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ssu.eatssu.domain.restaurant.entity.QRestaurant;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.restaurant.entity.RestaurantName;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FindRestaurantRepository {
    private final JPAQueryFactory queryFactory;
    private final QRestaurant restaurant = QRestaurant.restaurant;

    public Restaurant findByRestaurantName(RestaurantName name) {
        return queryFactory
                .selectFrom(restaurant)
                .where(
                        restaurantNameEq(name)
                )
                .fetchOne();
    }

    private BooleanExpression restaurantNameEq(RestaurantName name) {
        return restaurant.restaurantName.eq(name);
    }

    public Optional<Restaurant> findById(Long restaurantId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(restaurant)
                .where(
                        restaurant.id.eq(restaurantId)
                )
                .fetchOne());
    }
}
