package ssu.eatssu.domain.menu.entity;

import jakarta.persistence.*;
import java.util.HashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private Long id;

    @DateTimeFormat(pattern = "yyyyMMdd")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Enumerated(EnumType.STRING)
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    private TimePart timePart;

    private Integer price;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL)
    private List<MealMenu> mealMenus = new ArrayList<>();

    @ElementCollection
    private HashMap<String, Integer> countMap = new HashMap<>();

    public Meal(Date date, TimePart timePart, Restaurant restaurant, List<String> menuNames) {
        this.date = date;
        this.timePart = timePart;
        this.restaurant = restaurant;
        this.price = restaurant.getRestaurantPrice();

        initializeMap(menuNames);
    }

    public Meal(Date date, TimePart timePart, Restaurant restaurant, int price) {
        this.date = date;
        this.timePart = timePart;
        this.restaurant = restaurant;
        this.price = price;
    }

    private void initializeMap(List<String> menuNames) {
        for (String name : menuNames) {
            countMap.put(name, countMap.getOrDefault(name, 0) + 1);
        }
    }

    public List<String> getMenuNames() {
        return mealMenus.stream().map(mealMenu -> mealMenu.getMenu().getName()).toList();
    }
}
