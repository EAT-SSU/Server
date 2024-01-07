package ssu.eatssu.domain.rate;

import org.springframework.stereotype.Component;
import ssu.eatssu.domain.menu.Meal;
import ssu.eatssu.domain.menu.MealMenu;
import ssu.eatssu.domain.menu.Menu;
import ssu.eatssu.web.review.dto.AverageReviewRateResponse;
import ssu.eatssu.web.review.dto.ReviewRateCountResponse;

@Component
public class RateCalculator {

    // 식단에 포함된 메뉴들의 평점 별 개수 계산
    public ReviewRateCountResponse mealRateCount(Meal meal) {
        ReviewRate.resetAll();

        meal.getMealMenus().forEach(mealMenu -> {
            mealMenu.getMenu().getReviews().calculateReviewRates();
        });

        return ReviewRate.toResponse();
    }

    // 각 메뉴의 평점 별 개수 계산
    public ReviewRateCountResponse menuRateCount(Menu menu) {
        ReviewRate.resetAll();

        menu.getReviews().calculateReviewRates();

        return ReviewRate.toResponse();
    }


    // 식단 평균 평점 세트 계산
    public AverageReviewRateResponse mealAverageRate(Meal meal) {
        int totalReviewCount = meal.getTotalReviewCount();

        if (totalReviewCount == 0) {
            return new AverageReviewRateResponse(null, null, null);
        }

        return AverageReviewRateResponse.builder()
            .mainRate(averageRate(mealTotalMainRate(meal), totalReviewCount))
            .amountRate(averageRate(mealTotalAmountRate(meal), totalReviewCount))
            .tasteRate(averageRate(mealTotalTasteRate(meal), totalReviewCount))
            .build();
    }

    // 메뉴 평균 평점 세트 계산
    public AverageReviewRateResponse menuAverageRate(Menu menu) {
        int totalReviewCount = menu.getTotalReviewCount();

        if (totalReviewCount == 0) {
            return new AverageReviewRateResponse(null, null, null);
        }

        return AverageReviewRateResponse.builder()
            .mainRate(averageRate(menuTotalMainRate(menu), totalReviewCount))
            .amountRate(averageRate(menuTotalAmountRate(menu), totalReviewCount))
            .tasteRate(averageRate(menuTotalTasteRate(menu), totalReviewCount))
            .build();
    }

    // 식단 메인 평점 평균 계산
    public Double mealAverageMainRate(Meal meal) {
        int totalReviewCount = meal.getTotalReviewCount();

        if (totalReviewCount == 0) {
            return null;
        }

        return averageRate(mealTotalMainRate(meal), totalReviewCount);
    }

    // 메뉴 메인 평점 평균 계산
    public Double avarageMainRate(Menu menu) {
        if (menu.getReviews().size() == 0) {
            return null;
        }
        return averageRate(menuTotalMainRate(menu), menu.getReviews().size());
    }
    // 식단 총 리뷰 개수

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
            .map(menu -> menu.getReviews().getTotalMainRate())
            .reduce(0, Integer::sum);
    }


    // 식단 양 평점 총합
    private Integer mealTotalAmountRate(Meal meal) {
        return meal.getMealMenus().stream()
            .map(MealMenu::getMenu)
            .map(menu -> menu.getReviews().getTotalAmountRate())
            .reduce(0, Integer::sum);
    }


    // 식단 맛 평점 총합
    private Integer mealTotalTasteRate(Meal meal) {
        return meal.getMealMenus().stream()
            .map(MealMenu::getMenu)
            .map(menu -> menu.getReviews().getTotalTasteRate())
            .reduce(0, Integer::sum);
    }

    // 메뉴 메인 평점 총합
    private Integer menuTotalMainRate(Menu menu) {
        return menu.getReviews().getTotalMainRate();
    }

    // 메뉴 양 평점 총합
    private Integer menuTotalAmountRate(Menu menu) {
        return menu.getReviews().getTotalAmountRate();
    }

    // 메뉴 맛 평점 총합
    private Integer menuTotalTasteRate(Menu menu) {
        return menu.getReviews().getTotalTasteRate();
    }
}
