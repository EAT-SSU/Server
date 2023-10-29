package ssu.eatssu.domain;

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

    private static final long serialVersionUID = 2010434197L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeal meal = new QMeal("meal");

    public final NumberPath<Double> amountGrade = createNumber("amountGrade", Double.class);

    public final DatePath<java.util.Date> date = createDate("date", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> mainGrade = createNumber("mainGrade", Double.class);

    public final ListPath<MealMenu, QMealMenu> mealMenus = this.<MealMenu, QMealMenu>createList("mealMenus", MealMenu.class, QMealMenu.class, PathInits.DIRECT2);

    public final QRestaurant restaurant;

    public final NumberPath<Double> tasteGrade = createNumber("tasteGrade", Double.class);

    public final EnumPath<ssu.eatssu.domain.enums.TimePart> timePart = createEnum("timePart", ssu.eatssu.domain.enums.TimePart.class);

    public QMeal(String variable) {
        this(Meal.class, forVariable(variable), INITS);
    }

    public QMeal(Path<? extends Meal> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeal(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeal(PathMetadata metadata, PathInits inits) {
        this(Meal.class, metadata, inits);
    }

    public QMeal(Class<? extends Meal> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.restaurant = inits.isInitialized("restaurant") ? new QRestaurant(forProperty("restaurant")) : null;
    }

}

