package ssu.eatssu.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import ssu.eatssu.domain.enums.MenuTypeGroup;

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

    @ColumnDefault("0.0")
    private Double mainRate = 0.0;

    @ColumnDefault("0.0")
    private Double amountRate = 0.0;

    @ColumnDefault("0.0")
    private Double tasteRate = 0.0;

    @ColumnDefault("0")
    private Integer totalMainRate = 0;

    @ColumnDefault("0")
    private Integer totalAmountRate = 0;

    @ColumnDefault("0")
    private Integer totalTasteRate = 0;

    @ColumnDefault("0")
    private Integer reviewCnt = 0;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<MealMenu> mealMenus = new ArrayList<>();

    @ManyToOne(fetch=FetchType.LAZY)
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
        if(MenuTypeGroup.isChange(restaurant.getRestaurantName())){
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

    public void addReview(Integer mainRate, Integer tasteRate, Integer amountRate) {
            this.reviewCnt++;
            this.totalMainRate += mainRate;
            this.totalTasteRate += tasteRate;
            this.totalAmountRate += amountRate;
            calculateRate();
    }

    public void deleteReview(){
        refreshReview();
    }

    public void updateReview() {
        refreshReview();
    }

    public void refreshReview(){
        int totalMain = 0;
        int totalTaste =0;
        int totalAmount = 0;
        for(Review review : this.reviews){
            totalMain+=review.getMainRate();
            totalTaste += review.getTasteRate();
            totalAmount+=review.getAmountRate();
        }
        this.totalMainRate = totalMain;
        this.totalTasteRate = totalTaste;
        this.totalAmountRate = totalAmount;
        this.reviewCnt = (int)reviews.stream().count();
        calculateRate();
    }

    private void calculateRate(){ // 평점 계산 후 적용
        if(this.reviewCnt==0){
            rateReset();
        }else{
            this.mainRate = this.totalMainRate.doubleValue()/this.reviewCnt.doubleValue();
            this.tasteRate = this.totalTasteRate.doubleValue()/this.reviewCnt.doubleValue();
            this.amountRate = this.totalAmountRate.doubleValue()/this.reviewCnt.doubleValue();
        }
    }

    private void rateReset(){ //평점 초기화
        this.totalMainRate = 0;
        this.totalTasteRate = 0;
        this.totalAmountRate = 0;
        this.mainRate = 0.0;
        this.tasteRate = 0.0;
        this.amountRate = 0.0;
    }
}
