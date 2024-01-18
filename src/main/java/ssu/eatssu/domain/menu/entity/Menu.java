package ssu.eatssu.domain.menu.entity;

import jakarta.persistence.*;
import lombok.*;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.util.ArrayList;
import java.util.List;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.Reviews;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    private String name;

    private Integer price;

//    @Enumerated(EnumType.STRING)
//    private MenuType menuType;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<MealMenu> mealMenus = new ArrayList<>();

    private Restaurant restaurant;

    @Embedded
    private Reviews reviews = new Reviews();

    private Menu(String name, Restaurant restaurant, Integer price) {
        this.name = name;
        this.restaurant = restaurant;
        this.price = price;
    }

    public static Menu createVariable(String name, Restaurant restaurant) {
        int price = 0;
        if (restaurant.isVariable()) {
            price = restaurant.getRestaurantPrice();
        }
        return new Menu(name, restaurant, price);
    }

    public static Menu createFixed(String name, Restaurant restaurant, Integer price) {
        return new Menu(name, restaurant, price);
    }

    // COMMENT: CASCADE.PERSIST 를 사용하면, Menu 를 저장할 때 Review 도 같이 저장된다.
    public void addReview(Review review) {
        reviews.add(review);
    }

    public int getTotalReviewCount() {
        return reviews.size();
    }
}