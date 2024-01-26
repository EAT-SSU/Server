package ssu.eatssu.domain.menu.entity;

import jakarta.persistence.*;
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
    private TimePart timePart;

    @Enumerated(EnumType.STRING)
    private Restaurant restaurant;

    private String title;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL)
    private List<MealMenu> mealMenus = new ArrayList<>();

    private Meal(Date date, TimePart timePart, Restaurant restaurant, String title) {
        this.date = date;
        this.timePart = timePart;
        this.restaurant = restaurant;
        this.title = title;
    }

    public static Meal withTitle(Date date, TimePart timePart, Restaurant restaurant, String title) {
        return new Meal(date, timePart, restaurant, title);
    }

    public static Meal withoutTitle(Date date, TimePart timePart, Restaurant restaurant) {
        return new Meal(date, timePart, restaurant, null);
    }

    public List<String> getMenuNames() {
        return mealMenus.stream().map(mealMenu -> mealMenu.getMenu().getName()).toList();
    }

    public Double getAverateMainRating() {
        return mealMenus.stream()
                .mapToDouble(mealMenu -> mealMenu.getMenu().getReviews().getAverageMainRating())
                .average().orElse(0);
    }
}
