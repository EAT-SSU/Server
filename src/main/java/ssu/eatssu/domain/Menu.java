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
    private Double mainGrade = 0.0;

    @ColumnDefault("0.0")
    private Double amountGrade = 0.0;

    @ColumnDefault("0.0")
    private Double tasteGrade = 0.0;

    @ColumnDefault("0")
    private Integer totalMainGrade = 0;

    @ColumnDefault("0")
    private Integer totalAmountGrade = 0;

    @ColumnDefault("0")
    private Integer totalTasteGrade = 0;

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

    public void addReview(Integer mainGrade, Integer tasteGrade, Integer amountGrade) {
            this.reviewCnt++;
            this.totalMainGrade += mainGrade;
            this.totalTasteGrade += tasteGrade;
            this.totalAmountGrade += amountGrade;
            calculateGrade();
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
            totalMain+=review.getMainGrade();
            totalTaste = review.getTasteGrade();
            totalAmount+=review.getAmountGrade();
        }
        this.totalMainGrade = totalMain;
        this.totalTasteGrade = totalTaste;
        this.totalAmountGrade = totalAmount;
        this.reviewCnt = (int)reviews.stream().count();
        calculateGrade();
    }

    private void calculateGrade(){ // 평점 계산 후 적용
        if(this.reviewCnt!=0){
            this.mainGrade = this.totalMainGrade.doubleValue()/this.reviewCnt.doubleValue();
            this.tasteGrade = this.totalTasteGrade.doubleValue()/this.reviewCnt.doubleValue();
            this.amountGrade = this.totalAmountGrade.doubleValue()/this.reviewCnt.doubleValue();
        }
    }
}
