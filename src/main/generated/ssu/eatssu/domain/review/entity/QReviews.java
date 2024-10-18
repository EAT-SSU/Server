package ssu.eatssu.domain.review.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviews is a Querydsl query type for Reviews
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QReviews extends BeanPath<Reviews> {

    private static final long serialVersionUID = -1567053356L;

    public static final QReviews reviews1 = new QReviews("reviews1");

    public final ListPath<Review, QReview> reviews = this.<Review, QReview>createList("reviews", Review.class, QReview.class, PathInits.DIRECT2);

    public QReviews(String variable) {
        super(Reviews.class, forVariable(variable));
    }

    public QReviews(Path<? extends Reviews> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReviews(PathMetadata metadata) {
        super(Reviews.class, metadata);
    }

}

