package ssu.eatssu.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ssu.eatssu.domain.user.entity.BaseTimeEntity;
import ssu.eatssu.domain.user.entity.User;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_menu_favorite_push_history_user_date",
        columnNames = {"user_id", "push_date"}))
public class MenuFavoritePushHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_favorite_push_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "push_date", nullable = false)
    private LocalDate pushDate;

    private MenuFavoritePushHistory(User user, LocalDate pushDate) {
        this.user = user;
        this.pushDate = pushDate;
    }

    public static MenuFavoritePushHistory of(User user, LocalDate pushDate) {
        return new MenuFavoritePushHistory(user, pushDate);
    }
}
