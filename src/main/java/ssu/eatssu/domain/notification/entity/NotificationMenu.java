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
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.notification.service.MenuFavoriteItem;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Notification notification;

    @Column(name = "menu_name", nullable = false)
    private String menuName;

    @Column(name = "menu_name_en")
    private String menuNameEn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_part", nullable = false)
    private TimePart timePart;

    private NotificationMenu(Notification notification, MenuFavoriteItem item) {
        this.notification = notification;
        this.menuName = item.menuName();
        this.menuNameEn = item.menuNameEn();
        this.restaurant = item.restaurant();
        this.timePart = item.timePart();
    }

    static NotificationMenu of(Notification notification, MenuFavoriteItem item) {
        return new NotificationMenu(notification, item);
    }

    public MenuFavoriteItem toItem() {
        return new MenuFavoriteItem(menuName, menuNameEn, restaurant, timePart);
    }
}
