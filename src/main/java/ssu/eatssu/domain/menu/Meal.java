package ssu.eatssu.domain.menu;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ssu.eatssu.domain.restaurant.Restaurant;
import ssu.eatssu.domain.enums.TimePart;

import java.util.*;

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
    private TimePart timePart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL)
    private List<MealMenu> mealMenus = new ArrayList<>();

    @Builder
    public Meal(Date date, TimePart timePart, Restaurant restaurant) {
        this.date = date;
        this.timePart = timePart;
        this.restaurant = restaurant;
    }

    public int getTotalReviewCount() {
        return mealMenus.stream().mapToInt(mealMenu -> mealMenu.getMenu().getTotalReviewCount())
            .sum();
    }

    public List<Menu> getMenus() {
        List<Menu> menus = new ArrayList<>();
        for (MealMenu mealMenu : mealMenus) {
            menus.add(mealMenu.getMenu());
        }
        return menus;
    }

    public List<String> getMenuNames() {
        List<String> menuNames = new ArrayList<>();
        for (MealMenu mealMenu : mealMenus) {
            menuNames.add(mealMenu.getMenu().getName());
        }
        return menuNames;
    }

    public List<String> getMenuNameList() {
        return mealMenus.stream().map(mealMenu -> mealMenu.getMenu().getName()).toList();
    }
}
