package ssu.eatssu.domain.menu.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

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

    public List<String> getMenuNames() {
        return mealMenus.stream().map(mealMenu -> mealMenu.getMenu().getName()).toList();
    }

    public Double getAverateMainRating() {
        return mealMenus.stream()
            .mapToDouble(mealMenu -> mealMenu.getMenu().getReviews().getAverageMainRating())
            .average().orElse(0);
    }
}
