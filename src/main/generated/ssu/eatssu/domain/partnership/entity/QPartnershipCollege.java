package ssu.eatssu.domain.partnership.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPartnershipCollege is a Querydsl query type for PartnershipCollege
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPartnershipCollege extends EntityPathBase<PartnershipCollege> {

    private static final long serialVersionUID = -1865762014L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPartnershipCollege partnershipCollege = new QPartnershipCollege("partnershipCollege");

    public final ssu.eatssu.domain.user.department.entity.QCollege college;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPartnership partnership;

    public QPartnershipCollege(String variable) {
        this(PartnershipCollege.class, forVariable(variable), INITS);
    }

    public QPartnershipCollege(Path<? extends PartnershipCollege> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPartnershipCollege(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPartnershipCollege(PathMetadata metadata, PathInits inits) {
        this(PartnershipCollege.class, metadata, inits);
    }

    public QPartnershipCollege(Class<? extends PartnershipCollege> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.college = inits.isInitialized("college") ? new ssu.eatssu.domain.user.department.entity.QCollege(forProperty("college")) : null;
        this.partnership = inits.isInitialized("partnership") ? new QPartnership(forProperty("partnership")) : null;
    }

}

