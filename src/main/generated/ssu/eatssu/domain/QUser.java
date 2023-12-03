package ssu.eatssu.domain;

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

    private static final long serialVersionUID = 2010686109L;

    public static final QUser user = new QUser("user");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath nickname = createString("nickname");

    public final EnumPath<ssu.eatssu.domain.enums.OauthProvider> provider = createEnum("provider", ssu.eatssu.domain.enums.OauthProvider.class);

    public final StringPath providerId = createString("providerId");

    public final StringPath pwd = createString("pwd");

    public final ListPath<ReviewReport, QReviewReport> reviewReports = this.<ReviewReport, QReviewReport>createList("reviewReports", ReviewReport.class, QReviewReport.class, PathInits.DIRECT2);

    public final ListPath<Review, QReview> reviews = this.<Review, QReview>createList("reviews", Review.class, QReview.class, PathInits.DIRECT2);

    public final EnumPath<ssu.eatssu.domain.enums.Role> role = createEnum("role", ssu.eatssu.domain.enums.Role.class);

    public final EnumPath<ssu.eatssu.domain.enums.UserStatus> status = createEnum("status", ssu.eatssu.domain.enums.UserStatus.class);

    public final ListPath<UserInquiries, QUserInquiries> userInquiries = this.<UserInquiries, QUserInquiries>createList("userInquiries", UserInquiries.class, QUserInquiries.class, PathInits.DIRECT2);

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

