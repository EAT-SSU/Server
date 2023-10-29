package ssu.eatssu.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMenu is a Querydsl query type for Menu
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMenu extends EntityPathBase<Menu> {

    private static final long serialVersionUID = 2010434609L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMenu menu = new QMenu("menu");

    public final NumberPath<Double> amountGrade = createNumber("amountGrade", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> mainGrade = createNumber("mainGrade", Double.class);

    public final ListPath<MealMenu, QMealMenu> mealMenus = this.<MealMenu, QMealMenu>createList("mealMenus", MealMenu.class, QMealMenu.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final QRestaurant restaurant;

    public final NumberPath<Integer> reviewCnt = createNumber("reviewCnt", Integer.class);

    public final ListPath<Review, QReview> reviews = this.<Review, QReview>createList("reviews", Review.class, QReview.class, PathInits.DIRECT2);

    public final NumberPath<Double> tasteGrade = createNumber("tasteGrade", Double.class);

    public final NumberPath<Integer> totalAmountGrade = createNumber("totalAmountGrade", Integer.class);

    public final NumberPath<Integer> totalMainGrade = createNumber("totalMainGrade", Integer.class);

    public final NumberPath<Integer> totalTasteGrade = createNumber("totalTasteGrade", Integer.class);

    public QMenu(String variable) {
        this(Menu.class, forVariable(variable), INITS);
    }

    public QMenu(Path<? extends Menu> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMenu(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMenu(PathMetadata metadata, PathInits inits) {
        this(Menu.class, metadata, inits);
    }

    public QMenu(Class<? extends Menu> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.restaurant = inits.isInitialized("restaurant") ? new QRestaurant(forProperty("restaurant")) : null;
    }

}

