package ssu.eatssu.domain.user.entity;


import jakarta.persistence.*;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.Report;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

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

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE
            , CascadeType.REFRESH})
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<Report> reviewReports = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<Inquiry> userInquiries = new ArrayList<>();

    /**
     * Oauth 회원가입 용 생성자
     */
    private User(@NotNull String email, @NotNull Role role, @NotNull OAuthProvider provider,
                 @NotNull String providerId, @NotNull UserStatus status, @NotNull String credentials) {
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.status = status;
        this.credentials = credentials;
    }

    /**
     * <--Static Factory Method-->
     * Oauth 회원가입
     */
    public static User oAuthJoin(@NotNull String email, @NotNull OAuthProvider provider, String providerId,
                                 String credentials) {
        return new User(email, Role.USER, provider, providerId, UserStatus.ACTIVE, credentials);
    }

    public void updateNickname(@NotNull String nickname) {
        this.nickname = nickname;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

}
