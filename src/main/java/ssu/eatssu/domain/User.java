package ssu.eatssu.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ssu.eatssu.domain.enums.OauthProvider;
import ssu.eatssu.domain.enums.Role;
import ssu.eatssu.domain.enums.UserStatus;

import java.util.List;
import ssu.eatssu.domain.review.Review;

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

    private String password;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private OauthProvider provider;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.DETACH,
        CascadeType.MERGE
        , CascadeType.REFRESH})
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<ReviewReport> reviewReports;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<UserInquiry> userInquiry;

    private User(String email, String password) {
        this.email = email;
        this.password = password;
        this.role = Role.USER;
        this.provider = OauthProvider.EATSSU;
        this.status = UserStatus.ACTIVE;
    }

    private User(String email,OauthProvider provider, String providerId) {
        this.email = email;
        this.role = Role.USER;
        this.provider = provider;
        this.providerId = providerId;
        this.status = UserStatus.ACTIVE;
    }

    public static User oAuthJoin(@NotNull String email,
        @NotNull OauthProvider provider,
        String providerId) {
        return new User(email, provider, providerId);
    }


    public static User join(@NotNull String email, @NotNull String password) {
        return new User(email, password);
    }

    public void updateNickname(@NotNull String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

}
