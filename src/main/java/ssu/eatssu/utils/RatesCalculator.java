package ssu.eatssu.utils;

import org.springframework.stereotype.Component;
import ssu.eatssu.domain.Meal;
import ssu.eatssu.domain.MealMenu;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.vo.AverageRates;
import ssu.eatssu.vo.RateCountMap;

import java.util.HashMap;
import java.util.Map;

@Component
public class RatesCalculator {

    // 식단 각 평점별 개수 계산
    public RateCountMap mealRateCountMap(Meal meal) {

        RateCountMap rateCountMap = new RateCountMap();

        meal.getMealMenus().forEach(mealMenu -> rateCountMap.append(menuRateCountMap(mealMenu.getMenu())));

        return rateCountMap;
    }

    // 메뉴 각 평점별 개수 계산
    public RateCountMap menuRateCountMap(Menu menu) {
        Map<Integer, Integer> rateCountMap = new HashMap<>();

        menu.getReviews().forEach(review -> {
            int rate = review.getRates().getMainRate();
            rateCountMap.put(rate, rateCountMap.getOrDefault(rate, 0) + 1);
        });

        return new RateCountMap(rateCountMap);
    }

    // 식단 평균 평점 세트 계산
    public AverageRates mealAverageRates(Meal meal) {
        int totalReviewCount = mealTotalReviewCount(meal);

        if (totalReviewCount == 0)
            return new AverageRates(null, null, null);

        return AverageRates.builder()
                .mainRate(averageRate(mealTotalMainRate(meal), totalReviewCount))
                .amountRate(averageRate(mealTotalAmountRate(meal), totalReviewCount))
                .tasteRate(averageRate(mealTotalTasteRate(meal), totalReviewCount))
                .build();
    }

    // 메뉴 평균 평점 세트 계산
    public AverageRates menuAverageRates(Menu menu) {
        int totalReviewCount = menu.getReviews().size();

        if (totalReviewCount == 0)
            return new AverageRates(null, null, null);

        return AverageRates.builder()
                .mainRate(averageRate(menuTotalMainRate(menu), totalReviewCount))
                .amountRate(averageRate(menuTotalAmountRate(menu), totalReviewCount))
                .tasteRate(averageRate(menuTotalTasteRate(menu), totalReviewCount))
                .build();
    }

    // 식단 메인 평점 평균 계산
    public Double mealAverageMainRate(Meal meal) {
        int totalReviewCount = mealTotalReviewCount(meal);

        if (totalReviewCount == 0) {
            return null;
        }

        return averageRate(mealTotalMainRate(meal), totalReviewCount);
    }

    // 메뉴 메인 평점 평균 계산
    public Double menuAverageMainRate(Menu menu) {
        if (menu.getReviews().isEmpty()) {
            return null;
        }

        return averageRate(menuTotalMainRate(menu), menu.getReviews().size());
    }

    // 식단 총 리뷰 개수
    public int mealTotalReviewCount(Meal meal) {
        return (int) meal.getMealMenus().stream()
                .map(MealMenu::getMenu)
                .map(Menu::getReviews).count();
    }

    // 평균 평점 계산
    private Double averageRate(Integer totalRate, int totalReviewCount) {
        if (totalRate == null || totalReviewCount == 0) {
            return null;
        }
        return totalRate / (double) totalReviewCount;
    }

    // 식단 메인 평점 총합
    private Integer mealTotalMainRate(Meal meal) {
        return meal.getMealMenus().stream()
                .map(MealMenu::getMenu)
                .map(menu -> menu.getReviews().stream()
                        .map(review -> review.getRates().getMainRate())
                        .reduce(null, Integer::sum))
                .reduce(null, Integer::sum);
    }

    // 식단 양 평점 총합
    private Integer mealTotalAmountRate(Meal meal) {
        return meal.getMealMenus().stream()
                .map(MealMenu::getMenu)
                .map(menu -> menu.getReviews().stream()
                        .map(review -> review.getRates().getAmountRate())
                        .reduce(null, Integer::sum))
                .reduce(null, Integer::sum);
    }

    // 식단 맛 평점 총합
    private Integer mealTotalTasteRate(Meal meal) {
        return meal.getMealMenus().stream()
                .map(MealMenu::getMenu)
                .map(menu -> menu.getReviews().stream()
                        .map(review -> review.getRates().getTasteRate())
                        .reduce(null, Integer::sum))
                .reduce(null, Integer::sum);
    }

    // 메뉴 메인 평점 총합
    private Integer menuTotalMainRate(Menu menu) {
        return menu.getReviews().stream()
                .map(review -> review.getRates().getMainRate())
                .reduce(null, Integer::sum);
    }

    // 메뉴 양 평점 총합
    private Integer menuTotalAmountRate(Menu menu) {
        return menu.getReviews().stream()
                .map(review -> review.getRates().getAmountRate())
                .reduce(null, Integer::sum);
    }

    // 메뉴 맛 평점 총합
    private Integer menuTotalTasteRate(Menu menu) {
        return menu.getReviews().stream()
                .map(review -> review.getRates().getTasteRate())
                .reduce(null, Integer::sum);
    }

}