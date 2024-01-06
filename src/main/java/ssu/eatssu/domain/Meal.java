package ssu.eatssu.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ssu.eatssu.domain.enums.TimePart;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private Long id;

    //    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @DateTimeFormat(pattern = "yyyyMMdd")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Enumerated(EnumType.STRING)
    private TimePart timePart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private Double mainRate = 0.0;

    private Double amountRate = 0.0;

    private Double tasteRate = 0.0;


    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL)
    private List<MealMenu> mealMenus = new ArrayList<>();

    @Builder
    public Meal(Date date, TimePart timePart, Restaurant restaurant) {
        this.date = date;
        this.timePart = timePart;
        this.restaurant = restaurant;
    }

    public void caculateRate() {
        if (!mealMenus.isEmpty()) {
            int totalReviewCnt = 0;
            Double mainRateSum = 0.0;
            Double amountRateSum = 0.0;
            Double tasteRateSum = 0.0;
            for (MealMenu mealMenu : mealMenus) {
	Menu menu = mealMenu.getMenu();
	totalReviewCnt += menu.getReviewCount();
	mainRateSum += menu.getTotalMainRate();
	amountRateSum += menu.getTotalAmountRate();
	tasteRateSum += menu.getTotalTasteRate();
            }
            if (totalReviewCnt != 0) {
	this.mainRate = mainRateSum / totalReviewCnt;
	this.amountRate = amountRateSum / totalReviewCnt;
	this.tasteRate = tasteRateSum / totalReviewCnt;
            } else {
	this.mainRate = 0.0;
	this.amountRate = 0.0;
	this.tasteRate = 0.0;
            }

        }
    }

    public List<Menu> getMenus() {
        List<Menu> menus = new ArrayList<>();
        for (MealMenu mealMenu : mealMenus) {
            menus.add(mealMenu.getMenu());
        }
        return menus;
    }

    public List<String> getMenuNames() {
        List<String> menuNames = new ArrayList<>();
        for (MealMenu mealMenu : mealMenus) {
            menuNames.add(mealMenu.getMenu().getName());
        }
        return menuNames;
    }

    public Map<String, Double> getRateMap() {
        Map<String, Double> rateMap = new HashMap<>();
        rateMap.put("mainRate", mainRate);
        rateMap.put("amountRate", amountRate);
        rateMap.put("tasteRate", tasteRate);
        return rateMap;
    }

    public List<String> getMenuNameList() {
        return mealMenus.stream().map(mealMenu -> mealMenu.getMenu().getName()).toList();
    }
}
