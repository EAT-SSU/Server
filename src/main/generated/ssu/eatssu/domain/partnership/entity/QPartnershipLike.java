package ssu.eatssu.domain.partnership.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPartnershipLike is a Querydsl query type for PartnershipLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPartnershipLike extends EntityPathBase<PartnershipLike> {

    private static final long serialVersionUID = 1146206700L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPartnershipLike partnershipLike = new QPartnershipLike("partnershipLike");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPartnership partnership;

    public final ssu.eatssu.domain.user.entity.QUser user;

    public QPartnershipLike(String variable) {
        this(PartnershipLike.class, forVariable(variable), INITS);
    }

    public QPartnershipLike(Path<? extends PartnershipLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPartnershipLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPartnershipLike(PathMetadata metadata, PathInits inits) {
        this(PartnershipLike.class, metadata, inits);
    }

    public QPartnershipLike(Class<? extends PartnershipLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.partnership = inits.isInitialized("partnership") ? new QPartnership(forProperty("partnership")) : null;
        this.user = inits.isInitialized("user") ? new ssu.eatssu.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

