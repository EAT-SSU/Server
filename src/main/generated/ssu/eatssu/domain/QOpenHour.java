package ssu.eatssu.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOpenHour is a Querydsl query type for OpenHour
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOpenHour extends EntityPathBase<OpenHour> {

    private static final long serialVersionUID = -1320396512L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOpenHour openHour = new QOpenHour("openHour");

    public final EnumPath<ssu.eatssu.domain.enums.DayType> dayType = createEnum("dayType", ssu.eatssu.domain.enums.DayType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QRestaurant restaurant;

    public final StringPath time = createString("time");

    public final EnumPath<ssu.eatssu.domain.enums.TimePart> timePart = createEnum("timePart", ssu.eatssu.domain.enums.TimePart.class);

    public QOpenHour(String variable) {
        this(OpenHour.class, forVariable(variable), INITS);
    }

    public QOpenHour(Path<? extends OpenHour> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOpenHour(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOpenHour(PathMetadata metadata, PathInits inits) {
        this(OpenHour.class, metadata, inits);
    }

    public QOpenHour(Class<? extends OpenHour> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.restaurant = inits.isInitialized("restaurant") ? new QRestaurant(forProperty("restaurant")) : null;
    }

}

