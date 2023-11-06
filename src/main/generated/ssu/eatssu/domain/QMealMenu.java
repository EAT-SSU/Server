package ssu.eatssu.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMealMenu is a Querydsl query type for MealMenu
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMealMenu extends EntityPathBase<MealMenu> {

    private static final long serialVersionUID = -1799880300L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMealMenu mealMenu = new QMealMenu("mealMenu");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMeal meal;

    public final QMenu menu;

    public QMealMenu(String variable) {
        this(MealMenu.class, forVariable(variable), INITS);
    }

    public QMealMenu(Path<? extends MealMenu> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMealMenu(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMealMenu(PathMetadata metadata, PathInits inits) {
        this(MealMenu.class, metadata, inits);
    }

    public QMealMenu(Class<? extends MealMenu> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.meal = inits.isInitialized("meal") ? new QMeal(forProperty("meal"), inits.get("meal")) : null;
        this.menu = inits.isInitialized("menu") ? new QMenu(forProperty("menu"), inits.get("menu")) : null;
    }

}

