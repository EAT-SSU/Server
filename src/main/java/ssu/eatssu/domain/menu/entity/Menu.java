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

    @Enumerated(EnumType.STRING)
    private MenuType menuType;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<MealMenu> mealMenus = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Embedded
    private Reviews reviews = new Reviews();

    private Menu(String name, Restaurant restaurant, Integer price) {
        this.name = name;
        this.restaurant = restaurant;
        this.price = price;
    }

    /**
     * 변동 메뉴를 생성합니다.
     * todo: 변동메뉴 식당이 아니라 고정 메뉴 식당으로 잘못 들어온다면 어떻게 처리?
     */
    public static Menu createChangeMenu(String name, Restaurant restaurant) {
        int price = 0;
        if (MenuType.isChanged(restaurant.getRestaurantName())) {
            price = restaurant.getRestaurantName().getPrice();
        }
        return new Menu(name, restaurant, price);
    }

    public static Menu createFixMenu(String name, Restaurant restaurant, Integer price) {
        return new Menu(name, restaurant, price);
    }

    /**
     * 고정 메뉴를 생성합니다.
     * todo: 고정메뉴 식당이 아니라 변동 메뉴 식당으로 잘못 들어온다면 어떻게 처리?
     */
    public static Menu createFixedMenu(String name, Restaurant restaurant, Integer price) {
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