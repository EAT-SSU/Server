package ssu.eatssu.domain.menu.entity;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.Reviews;

import java.util.ArrayList;
import java.util.List;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @Column(name = "sorted_index")
    private Integer soretedIndex;

    private String name;

    private Integer price;

    private boolean isDiscontinued = false;

    @Enumerated(EnumType.STRING)
    private Restaurant restaurant;

    @Embedded
    private Reviews reviews = new Reviews();

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealMenu> mealMenus = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_category_id")
    private MenuCategory category;


    private Menu(String name, Restaurant restaurant, Integer price, MenuCategory category) {
        this.name = name;
        this.restaurant = restaurant;
        this.price = price;
        this.category = category;
    }

    public static Menu createVariable(String name, Restaurant restaurant) {
        Assert.isTrue(restaurant.isVariableType(), "변동 메뉴 식당이 아닙니다.");
        final int price = 0;
        return new Menu(name, restaurant, price, null);
    }

    public static Menu createFixed(String name, Restaurant restaurant, Integer price,
        MenuCategory category) {
        Assert.isTrue(restaurant.isFixedType(), "고정 메뉴 식당이 아닙니다.");
        return new Menu(name, restaurant, price, category);
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public int getTotalReviewCount() {
        return reviews.size();
    }

    public void update(final String name, final Integer price) {
        this.name = name;
        this.price = price;
    }

    public void changeDiscontinuedStatus() {
        this.isDiscontinued = !this.isDiscontinued;
    }
}