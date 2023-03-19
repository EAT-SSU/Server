package ssu.eatssu.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    private String email;

    @NotNull
    private String pwd;

    @NotNull
    private String nickname;

    public static User join(@NotNull String email, @NotNull String pwd, @NotNull String nickname) {
        User user = new User();
        user.email = email;
        user.pwd = pwd;
        user.nickname = nickname;
        user.role = Role.USER;
        return user;
    }

    public void updateNickname(@NotNull String nickname) {
        this.nickname = nickname;
    }
}
