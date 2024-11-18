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

    public static final QUser user = new QUser("user");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath credentials = createString("credentials");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath nickname = createString("nickname");

    public final EnumPath<ssu.eatssu.domain.auth.entity.OAuthProvider> provider = createEnum("provider", ssu.eatssu.domain.auth.entity.OAuthProvider.class);

    public final StringPath providerId = createString("providerId");

    public final ListPath<ssu.eatssu.domain.review.entity.Report, ssu.eatssu.domain.review.entity.QReport> reviewReports = this.<ssu.eatssu.domain.review.entity.Report, ssu.eatssu.domain.review.entity.QReport>createList("reviewReports", ssu.eatssu.domain.review.entity.Report.class, ssu.eatssu.domain.review.entity.QReport.class, PathInits.DIRECT2);

    public final ListPath<ssu.eatssu.domain.review.entity.Review, ssu.eatssu.domain.review.entity.QReview> reviews = this.<ssu.eatssu.domain.review.entity.Review, ssu.eatssu.domain.review.entity.QReview>createList("reviews", ssu.eatssu.domain.review.entity.Review.class, ssu.eatssu.domain.review.entity.QReview.class, PathInits.DIRECT2);

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final EnumPath<UserStatus> status = createEnum("status", UserStatus.class);

    public final ListPath<ssu.eatssu.domain.inquiry.entity.Inquiry, ssu.eatssu.domain.inquiry.entity.QInquiry> userInquiries = this.<ssu.eatssu.domain.inquiry.entity.Inquiry, ssu.eatssu.domain.inquiry.entity.QInquiry>createList("userInquiries", ssu.eatssu.domain.inquiry.entity.Inquiry.class, ssu.eatssu.domain.inquiry.entity.QInquiry.class, PathInits.DIRECT2);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

