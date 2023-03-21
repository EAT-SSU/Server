package ssu.eatssu.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private RestaurantName restaurantName;


    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Menu> menus;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<TodayMenu> todayMenus;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<OpenHour> openHours;
}
