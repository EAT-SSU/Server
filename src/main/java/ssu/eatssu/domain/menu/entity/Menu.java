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

    // TODO : 삭제되어야 함
    @Embedded
    private Reviews reviews = new Reviews();

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<MealMenu> mealMenus = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_category_id")
    private MenuCategory category;

    private boolean isDiscontinued = false;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "unlike_count")
    private Integer unlikeCount = 0;

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

    public static Menu createFixed(String name, Restaurant restaurant, Integer price,
        MenuCategory category) {
        return new Menu(name, restaurant, price, category);
    }

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

    public void changeDiscontinuedStatus() {
        this.isDiscontinued = !this.isDiscontinued;
    }

    public boolean isContinued() {
        return !this.isDiscontinued;
    }

    public void increaseLikeCount() {
        likeCount++;
    }

    public void increaseUnlikeCount() {
        unlikeCount++;
    }

    public void decreaseLikeCount() {
        likeCount--;
    }

    public void decreaseUnlikeCount() {
        unlikeCount--;
    }

    public void changeLikeStatus(Boolean isLike) {
        if (isLike) {
            decreaseUnlikeCount();
            increaseLikeCount();
        } else {
            decreaseLikeCount();
            increaseUnlikeCount();
        }
    }

    public void adjustLikeCount(int count, Boolean isLike) {
        if (isLike) {
            this.likeCount += count;
        } else {
            this.unlikeCount += count;
        }
    }
}