package ssu.eatssu.domain.review.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewMenuLike is a Querydsl query type for ReviewMenuLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewMenuLike extends EntityPathBase<ReviewMenuLike> {

    private static final long serialVersionUID = -1523380939L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewMenuLike reviewMenuLike = new QReviewMenuLike("reviewMenuLike");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isLike = createBoolean("isLike");

    public final ssu.eatssu.domain.menu.entity.QMenu menu;

    public final QReview review;

    public QReviewMenuLike(String variable) {
        this(ReviewMenuLike.class, forVariable(variable), INITS);
    }

    public QReviewMenuLike(Path<? extends ReviewMenuLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewMenuLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewMenuLike(PathMetadata metadata, PathInits inits) {
        this(ReviewMenuLike.class, metadata, inits);
    }

    public QReviewMenuLike(Class<? extends ReviewMenuLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.menu = inits.isInitialized("menu") ? new ssu.eatssu.domain.menu.entity.QMenu(forProperty("menu"), inits.get("menu")) : null;
        this.review = inits.isInitialized("review") ? new QReview(forProperty("review"), inits.get("review")) : null;
    }

}

