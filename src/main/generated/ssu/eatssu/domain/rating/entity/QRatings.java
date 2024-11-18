package ssu.eatssu.domain.rating.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRatings is a Querydsl query type for Ratings
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QRatings extends BeanPath<Ratings> {

    private static final long serialVersionUID = 514329418L;

    public static final QRatings ratings = new QRatings("ratings");

    public final NumberPath<Integer> amountRating = createNumber("amountRating", Integer.class);

    public final NumberPath<Integer> mainRating = createNumber("mainRating", Integer.class);

    public final NumberPath<Integer> tasteRating = createNumber("tasteRating", Integer.class);

    public QRatings(String variable) {
        super(Ratings.class, forVariable(variable));
    }

    public QRatings(Path<? extends Ratings> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRatings(PathMetadata metadata) {
        super(Ratings.class, metadata);
    }

}

