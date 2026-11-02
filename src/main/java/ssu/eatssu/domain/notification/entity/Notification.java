package ssu.eatssu.domain.notification.entity;

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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ssu.eatssu.domain.notification.service.MenuFavoriteItem;
import ssu.eatssu.domain.user.entity.BaseTimeEntity;
import ssu.eatssu.domain.user.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_notification_user_type_date",
        columnNames = {"user_id", "type", "target_date"}))
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<NotificationMenu> menus = new ArrayList<>();

    private Notification(User user, NotificationType type, LocalDate targetDate) {
        this.user = user;
        this.type = type;
        this.targetDate = targetDate;
        this.isRead = false;
    }

    public static Notification menuFavorite(User user, LocalDate targetDate, List<MenuFavoriteItem> items) {
        Notification notification = new Notification(user, NotificationType.MENU_FAVORITE, targetDate);
        items.forEach(item -> notification.menus.add(NotificationMenu.of(notification, item)));
        return notification;
    }

    public void read() {
        this.isRead = true;
    }

    public List<MenuFavoriteItem> toItems() {
        return menus.stream().map(NotificationMenu::toItem).toList();
    }
}
