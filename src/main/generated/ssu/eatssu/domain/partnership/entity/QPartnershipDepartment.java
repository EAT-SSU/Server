package ssu.eatssu.domain.partnership.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPartnershipDepartment is a Querydsl query type for PartnershipDepartment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPartnershipDepartment extends EntityPathBase<PartnershipDepartment> {

    private static final long serialVersionUID = 841972199L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPartnershipDepartment partnershipDepartment = new QPartnershipDepartment("partnershipDepartment");

    public final ssu.eatssu.domain.department.entity.QDepartment department;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPartnership partnership;

    public QPartnershipDepartment(String variable) {
        this(PartnershipDepartment.class, forVariable(variable), INITS);
    }

    public QPartnershipDepartment(Path<? extends PartnershipDepartment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPartnershipDepartment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPartnershipDepartment(PathMetadata metadata, PathInits inits) {
        this(PartnershipDepartment.class, metadata, inits);
    }

    public QPartnershipDepartment(Class<? extends PartnershipDepartment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.department = inits.isInitialized("department") ? new ssu.eatssu.domain.department.entity.QDepartment(forProperty("department"), inits.get("department")) : null;
        this.partnership = inits.isInitialized("partnership") ? new QPartnership(forProperty("partnership")) : null;
    }

}

