package ssu.eatssu.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRestaurant is a Querydsl query type for Restaurant
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRestaurant extends EntityPathBase<Restaurant> {

    private static final long serialVersionUID = 607657103L;

    public static final QRestaurant restaurant = new QRestaurant("restaurant");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath location = createString("location");

    public final ListPath<Meal, QMeal> meals = this.<Meal, QMeal>createList("meals", Meal.class, QMeal.class, PathInits.DIRECT2);

    public final ListPath<Menu, QMenu> menus = this.<Menu, QMenu>createList("menus", Menu.class, QMenu.class, PathInits.DIRECT2);

    public final ListPath<OpenHour, QOpenHour> openHours = this.<OpenHour, QOpenHour>createList("openHours", OpenHour.class, QOpenHour.class, PathInits.DIRECT2);

    public final EnumPath<ssu.eatssu.domain.enums.RestaurantName> restaurantName = createEnum("restaurantName", ssu.eatssu.domain.enums.RestaurantName.class);

    public QRestaurant(String variable) {
        super(Restaurant.class, forVariable(variable));
    }

    public QRestaurant(Path<? extends Restaurant> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRestaurant(PathMetadata metadata) {
        super(Restaurant.class, metadata);
    }

}

