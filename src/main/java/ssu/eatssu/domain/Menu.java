package ssu.eatssu.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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
    private Integer reviewCnt = 0 ;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<MealMenu> mealMenus = new ArrayList<>();

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    private Menu(String name, Restaurant restaurant, Integer price) {
        this.name = name;
        this.restaurant = restaurant;
        this.price = price;
    }

    public static Menu addFixPrice(String name, Restaurant restaurant) {
        Integer price = 0;
        if(!restaurant.getRestaurantName().getPrice().equals(0)){
            price = restaurant.getRestaurantName().getPrice();
        }
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
