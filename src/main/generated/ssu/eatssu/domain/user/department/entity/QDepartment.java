package ssu.eatssu.domain.user.department.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDepartment is a Querydsl query type for Department
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDepartment extends EntityPathBase<Department> {

    private static final long serialVersionUID = 2118910098L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDepartment department = new QDepartment("department");

    public final QCollege college;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<ssu.eatssu.domain.partnership.entity.PartnershipDepartment, ssu.eatssu.domain.partnership.entity.QPartnershipDepartment> partnershipDepartments = this.<ssu.eatssu.domain.partnership.entity.PartnershipDepartment, ssu.eatssu.domain.partnership.entity.QPartnershipDepartment>createList("partnershipDepartments", ssu.eatssu.domain.partnership.entity.PartnershipDepartment.class, ssu.eatssu.domain.partnership.entity.QPartnershipDepartment.class, PathInits.DIRECT2);

    public QDepartment(String variable) {
        this(Department.class, forVariable(variable), INITS);
    }

    public QDepartment(Path<? extends Department> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDepartment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDepartment(PathMetadata metadata, PathInits inits) {
        this(Department.class, metadata, inits);
    }

    public QDepartment(Class<? extends Department> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.college = inits.isInitialized("college") ? new QCollege(forProperty("college")) : null;
    }

}

