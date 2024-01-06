package ssu.eatssu.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.enums.MenuTypeGroup;
import ssu.eatssu.domain.review.Review;

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

    private Integer price;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<MealMenu> mealMenus = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    //정적 팩토리 메서드
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
        if (MenuTypeGroup.isChange(restaurant.getRestaurantName())) {
            price = restaurant.getRestaurantName().getPrice();
        }
        return new Menu(name, restaurant, price);
    }

    /**
     * 고정 메뉴를 생성합니다.
     * todo: 고정메뉴 식당이 아니라 변동 메뉴 식당으로 잘못 들어온다면 어떻게 처리?
     */
    public static Menu createFixedMenu(String name, Restaurant restaurant, Integer price) {
        return new Menu(name, restaurant, price);
    }
}
