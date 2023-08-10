package ssu.eatssu.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ssu.eatssu.domain.enums.OauthProvider;
import ssu.eatssu.domain.enums.Role;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true)
    private String email;

    @NotNull
    private String pwd;

    @NotNull
    private String nickname;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OauthProvider provider;

    private String providerId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews;

    public static User join(@NotNull String email, @NotNull String pwd, @NotNull String nickname) {
        User user = new User();
        user.email = email;
        user.pwd = pwd;
        user.nickname = nickname;
        user.role = Role.USER;
        user.provider = OauthProvider.EATSSU;
        return user;
    }
    public static User oAuthJoin(@NotNull String email, @NotNull String pwd, @NotNull String nickname,
                            @NotNull OauthProvider provider, String providerId) {
        User user = new User();
        user.email = email;
        user.pwd = pwd;
        user.nickname = nickname;
        user.role = Role.USER;
        user.provider = provider;
        user.providerId = providerId;
        return user;
    }

    public void updateNickname(@NotNull String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String newPwd) {
        this.pwd = newPwd;
    }
}
