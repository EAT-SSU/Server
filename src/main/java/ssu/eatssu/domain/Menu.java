package ssu.eatssu.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    private String name;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private Integer price;

    private Double grade;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<TodayMenu> todayMenus = new ArrayList<>();

    private Menu(String name, Restaurant restaurant, Integer price) {
        this.name = name;
        this.restaurant = restaurant;
        this.price = price;
        grade = null;
    }

    public static Menu addFixPrice(String name, Restaurant restaurant) {
        Integer price = 0;
        if(!restaurant.getRestaurantName().getPrice().equals(0)){
            price = restaurant.getRestaurantName().getPrice();
        }
        return new Menu(name, restaurant, price);
    }

}
