package ssu.eatssu.domain.menu.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMenuCategory is a Querydsl query type for MenuCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMenuCategory extends EntityPathBase<MenuCategory> {

    private static final long serialVersionUID = -2082482581L;

    public static final QMenuCategory menuCategory = new QMenuCategory("menuCategory");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final EnumPath<ssu.eatssu.domain.restaurant.entity.Restaurant> restaurant = createEnum("restaurant", ssu.eatssu.domain.restaurant.entity.Restaurant.class);

    public QMenuCategory(String variable) {
        super(MenuCategory.class, forVariable(variable));
    }

    public QMenuCategory(Path<? extends MenuCategory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMenuCategory(PathMetadata metadata) {
        super(MenuCategory.class, metadata);
    }

}

