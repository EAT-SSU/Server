package ssu.eatssu.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -2112045787L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath credentials = createString("credentials");

    public final ssu.eatssu.domain.user.department.entity.QDepartment department;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath nickname = createString("nickname");

    public final ListPath<ssu.eatssu.domain.partnership.entity.PartnershipLike, ssu.eatssu.domain.partnership.entity.QPartnershipLike> partnershipLikes = this.<ssu.eatssu.domain.partnership.entity.PartnershipLike, ssu.eatssu.domain.partnership.entity.QPartnershipLike>createList("partnershipLikes", ssu.eatssu.domain.partnership.entity.PartnershipLike.class, ssu.eatssu.domain.partnership.entity.QPartnershipLike.class, PathInits.DIRECT2);

    public final EnumPath<ssu.eatssu.domain.auth.entity.OAuthProvider> provider = createEnum("provider", ssu.eatssu.domain.auth.entity.OAuthProvider.class);

    public final StringPath providerId = createString("providerId");

    public final ListPath<ssu.eatssu.domain.review.entity.ReviewLike, ssu.eatssu.domain.review.entity.QReviewLike> reviewLikes = this.<ssu.eatssu.domain.review.entity.ReviewLike, ssu.eatssu.domain.review.entity.QReviewLike>createList("reviewLikes", ssu.eatssu.domain.review.entity.ReviewLike.class, ssu.eatssu.domain.review.entity.QReviewLike.class, PathInits.DIRECT2);

    public final ListPath<ssu.eatssu.domain.review.entity.Report, ssu.eatssu.domain.review.entity.QReport> reviewReports = this.<ssu.eatssu.domain.review.entity.Report, ssu.eatssu.domain.review.entity.QReport>createList("reviewReports", ssu.eatssu.domain.review.entity.Report.class, ssu.eatssu.domain.review.entity.QReport.class, PathInits.DIRECT2);

    public final ListPath<ssu.eatssu.domain.review.entity.Review, ssu.eatssu.domain.review.entity.QReview> reviews = this.<ssu.eatssu.domain.review.entity.Review, ssu.eatssu.domain.review.entity.QReview>createList("reviews", ssu.eatssu.domain.review.entity.Review.class, ssu.eatssu.domain.review.entity.QReview.class, PathInits.DIRECT2);

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final EnumPath<UserStatus> status = createEnum("status", UserStatus.class);

    public final ListPath<ssu.eatssu.domain.inquiry.entity.Inquiry, ssu.eatssu.domain.inquiry.entity.QInquiry> userInquiries = this.<ssu.eatssu.domain.inquiry.entity.Inquiry, ssu.eatssu.domain.inquiry.entity.QInquiry>createList("userInquiries", ssu.eatssu.domain.inquiry.entity.Inquiry.class, ssu.eatssu.domain.inquiry.entity.QInquiry.class, PathInits.DIRECT2);

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.department = inits.isInitialized("department") ? new ssu.eatssu.domain.user.department.entity.QDepartment(forProperty("department"), inits.get("department")) : null;
    }

}

