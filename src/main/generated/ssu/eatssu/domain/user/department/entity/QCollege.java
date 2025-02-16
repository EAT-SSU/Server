package ssu.eatssu.domain.user.department.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCollege is a Querydsl query type for College
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCollege extends EntityPathBase<College> {

    private static final long serialVersionUID = 498379863L;

    public static final QCollege college = new QCollege("college");

    public final ListPath<Department, QDepartment> departments = this.<Department, QDepartment>createList("departments", Department.class, QDepartment.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<ssu.eatssu.domain.partnership.entity.PartnershipCollege, ssu.eatssu.domain.partnership.entity.QPartnershipCollege> partnershipColleges = this.<ssu.eatssu.domain.partnership.entity.PartnershipCollege, ssu.eatssu.domain.partnership.entity.QPartnershipCollege>createList("partnershipColleges", ssu.eatssu.domain.partnership.entity.PartnershipCollege.class, ssu.eatssu.domain.partnership.entity.QPartnershipCollege.class, PathInits.DIRECT2);

    public QCollege(String variable) {
        super(College.class, forVariable(variable));
    }

    public QCollege(Path<? extends College> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCollege(PathMetadata metadata) {
        super(College.class, metadata);
    }

}

