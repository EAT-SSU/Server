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

    private String pwd;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private OauthProvider provider;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.DETACH,CascadeType.MERGE
            ,CascadeType.REFRESH})
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<ReviewReport> reviewReports;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<UserInquiry> userInquiry;

    public static User join(@NotNull String email, @NotNull String pwd) {
        User user = new User();
        user.email = email;
        user.pwd = pwd;
        user.role = Role.USER;
        user.provider = OauthProvider.EATSSU;
        user.status = UserStatus.ACTIVE;
        return user;
    }
    public static User oAuthJoin(@NotNull String email, @NotNull String pwd, @NotNull OauthProvider provider,
                                 String providerId) {
        User user = new User();
        user.email = email;
        user.pwd = pwd;
        user.role = Role.USER;
        user.provider = provider;
        user.providerId = providerId;
        user.status = UserStatus.ACTIVE;
        return user;
    }

    public void updateNickname(@NotNull String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String newPwd) {
        this.pwd = newPwd;
    }

    public void updateEmail(String email) { this.email = email; }

}
