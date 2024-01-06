package ssu.eatssu.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ssu.eatssu.domain.enums.OauthProvider;
import ssu.eatssu.domain.enums.Role;
import ssu.eatssu.domain.enums.UserStatus;
import ssu.eatssu.domain.review.Review;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true)
    private String email;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private OauthProvider provider;

    private String providerId;

    private String credentials;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.DETACH,CascadeType.MERGE
            ,CascadeType.REFRESH})
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<ReviewReport> reviewReports;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<UserInquiry> userInquiry;


    /**
     * Oauth 회원가입 용 생성자
     */
    private User(@NotNull String email, @NotNull Role role, @NotNull OauthProvider provider,
                 @NotNull String providerId, @NotNull UserStatus status, @NotNull String credentials){
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
    public static User oAuthJoin(@NotNull String email, @NotNull OauthProvider provider, String providerId,
                                 String credentials) {
        return new User(email, Role.USER,  provider, providerId, UserStatus.ACTIVE, credentials);
    }

    public void updateNickname(@NotNull String nickname) {
        this.nickname = nickname;
    }

    public void updateEmail(String email) { this.email = email; }

}
