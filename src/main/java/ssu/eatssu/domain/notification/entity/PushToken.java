package ssu.eatssu.domain.notification.entity;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ssu.eatssu.domain.user.entity.BaseTimeEntity;
import ssu.eatssu.domain.user.entity.DeviceType;
import ssu.eatssu.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "push_token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType deviceType;

    private PushToken(User user, String token, DeviceType deviceType) {
        this.user = user;
        this.token = token;
        this.deviceType = deviceType;
    }

    public static PushToken of(User user, String token, DeviceType deviceType) {
        return new PushToken(user, token, deviceType);
    }

    public void reassign(User user, DeviceType deviceType) {
        this.user = user;
        this.deviceType = deviceType;
    }
}
