package ssu.eatssu.domain.menu.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.Reviews;

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

    @Enumerated(EnumType.STRING)
    private Restaurant restaurant;

    @Embedded
    private Reviews reviews = new Reviews();

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<MealMenu> mealMenus = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_category_id")
    private MenuCategory category;

    private boolean isDiscontinued = false;

    private Menu(String name, Restaurant restaurant, Integer price, MenuCategory category) {
        this.name = name;
        this.restaurant = restaurant;
        this.price = price;
        this.category = category;
    }

    public static Menu createVariable(String name, Restaurant restaurant) {
        int price = 0;
        return new Menu(name, restaurant, price, null);
    }

    /**
     * 고정 메뉴를 생성합니다.
     * todo: 고정메뉴 식당이 아니라 변동 메뉴 식당으로 잘못 들어온다면 어떻게 처리?
     */
    public static Menu createFixed(String name, Restaurant restaurant, Integer price,
                                   MenuCategory category) {
        return new Menu(name, restaurant, price, category);
    }

    // COMMENT: CASCADE.PERSIST 를 사용하면, Menu 를 저장할 때 Review 도 같이 저장된다.
    public void addReview(Review review) {
        reviews.add(review);
    }

    public int getTotalReviewCount() {
        return reviews.size();
    }

    public void update(String name, Integer price) {
        this.name = name;
        this.price = price;
    }
}