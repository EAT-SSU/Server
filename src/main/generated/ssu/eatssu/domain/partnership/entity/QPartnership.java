package ssu.eatssu.domain.partnership.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPartnership is a Querydsl query type for Partnership
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPartnership extends EntityPathBase<Partnership> {

    private static final long serialVersionUID = 477296821L;

    public static final QPartnership partnership = new QPartnership("partnership");

    public final StringPath description = createString("description");

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final ListPath<PartnershipLike, QPartnershipLike> likes = this.<PartnershipLike, QPartnershipLike>createList("likes", PartnershipLike.class, QPartnershipLike.class, PathInits.DIRECT2);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final ListPath<PartnershipCollege, QPartnershipCollege> partnershipColleges = this.<PartnershipCollege, QPartnershipCollege>createList("partnershipColleges", PartnershipCollege.class, QPartnershipCollege.class, PathInits.DIRECT2);

    public final ListPath<PartnershipDepartment, QPartnershipDepartment> partnershipDepartments = this.<PartnershipDepartment, QPartnershipDepartment>createList("partnershipDepartments", PartnershipDepartment.class, QPartnershipDepartment.class, PathInits.DIRECT2);

    public final EnumPath<PartnershipType> partnershipType = createEnum("partnershipType", PartnershipType.class);

    public final EnumPath<RestaurantType> restaurantType = createEnum("restaurantType", RestaurantType.class);

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final StringPath storeName = createString("storeName");

    public QPartnership(String variable) {
        super(Partnership.class, forVariable(variable));
    }

    public QPartnership(Path<? extends Partnership> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPartnership(PathMetadata metadata) {
        super(Partnership.class, metadata);
    }

}

