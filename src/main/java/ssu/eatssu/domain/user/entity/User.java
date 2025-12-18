package ssu.eatssu.domain.user.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.partnership.entity.PartnershipLike;
import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.ReviewLike;
import ssu.eatssu.domain.user.department.entity.Department;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE
            , CascadeType.REFRESH})
    private final List<Review> reviews = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private final List<Report> reviewReports = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private final List<Inquiry> userInquiries = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private final List<ReviewLike> reviewLikes = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private final List<PartnershipLike> partnershipLikes = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(unique = true)
    private String email;
    private String nickname;
    @Enumerated(EnumType.STRING)
    private OAuthProvider provider;
    private String providerId;
    private String credentials;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * Oauth 회원가입 용 생성자
     */
    private User(@NotNull String email, String nickname, @NotNull Role role, @NotNull OAuthProvider provider,
                 @NotNull String providerId, @NotNull UserStatus status, @NotNull String credentials, DeviceType deviceType) {
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.status = status;
        this.credentials = credentials;
        this.deviceType = deviceType;
    }

    // TODO : 회원 가입 V2 마이그레이션 이후 삭제.
    //  기본 생성자 : DeviceType을 Null로 받고 있는데 마이그레이션 이후 기본 생성자도 NotNull 붙이기.
    //  admin 정적 펙토리 : 여기도 null 넣는 부분 바꿔주기.
    /**
     * <--Static Factory Method-->
     * Oauth 회원가입
     */
    public static User create(@NotNull String email, @NotNull String nickname, @NotNull OAuthProvider provider,
                              String providerId,
                              String credentials) {
        return new User(email, nickname, Role.USER, provider, providerId, UserStatus.ACTIVE, credentials, null);
    }

    /**
     * <--Static Factory Method-->
     * Oauth 회원가입 V2
     */
    public static User createV2(@NotNull String email, @NotNull String nickname, @NotNull OAuthProvider provider,
                                String providerId,
                                String credentials,
                                DeviceType deviceType) {
        return new User(email, nickname, Role.USER, provider, providerId, UserStatus.ACTIVE, credentials, deviceType);
    }

    /**
     * <--Static Factory Method-->
     * admin 회원가입
     * Role 은 다른 방법으로 세팅할 예정
     */
    public static User adminJoin(@NotNull String loginId, @NotNull String credentials) {
        return new User(loginId, null, Role.USER, OAuthProvider.EATSSU, loginId, UserStatus.INACTIVE, credentials, null);
    }

    public void updateNickname(@NotNull String nickname) {
        this.nickname = nickname;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateDepartment(Department department) {
        this.department = department;
    }

}
