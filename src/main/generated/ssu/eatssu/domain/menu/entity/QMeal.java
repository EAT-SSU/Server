package ssu.eatssu.domain.menu.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeal is a Querydsl query type for Meal
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeal extends EntityPathBase<Meal> {

    private static final long serialVersionUID = 1205817521L;

    public static final QMeal meal = new QMeal("meal");

    public final DatePath<java.util.Date> date = createDate("date", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<MealMenu, QMealMenu> mealMenus = this.<MealMenu, QMealMenu>createList("mealMenus", MealMenu.class, QMealMenu.class, PathInits.DIRECT2);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final EnumPath<ssu.eatssu.domain.restaurant.entity.Restaurant> restaurant = createEnum("restaurant", ssu.eatssu.domain.restaurant.entity.Restaurant.class);

    public final EnumPath<ssu.eatssu.domain.menu.entity.constants.TimePart> timePart = createEnum("timePart", ssu.eatssu.domain.menu.entity.constants.TimePart.class);

    public QMeal(String variable) {
        super(Meal.class, forVariable(variable));
    }

    public QMeal(Path<? extends Meal> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMeal(PathMetadata metadata) {
        super(Meal.class, metadata);
    }

}

