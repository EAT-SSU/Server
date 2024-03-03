package ssu.eatssu.domain.rating.entity;

import org.springframework.stereotype.Component;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.review.dto.RatingAverages;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;

/**
 * JPA 의 OneToMany객체참조를 이용하여 구현한 클래스입니다.
 */
public class JpaLoadCollectionRatingCalculator implements RatingCalculator {

    // 식단에 포함된 메뉴들의 평점 별 리뷰 개수 계산
    public ReviewRatingCount mealRatingCount(Meal meal) {
        ReviewRating.resetAll();

        meal.getMealMenus().forEach(mealMenu -> mealMenu.getMenu().getReviews().calculateReviewRatings());

        return ReviewRating.toResponse();
    }

    // 각 메뉴의 평점 별 개수 계산
    public ReviewRatingCount menuRatingCount(Menu menu) {
        ReviewRating.resetAll();

        menu.getReviews().calculateReviewRatings();

        return ReviewRating.toResponse();
    }

    // 식단 평균 평점 세트 계산
    public RatingAverages mealAverageRatings(Meal meal) {
        long totalReviewCount = mealTotalReviewCount(meal);

        if (totalReviewCount == 0)
            return new RatingAverages(null, null, null);

        return RatingAverages.builder()
                .mainRating(averageRating(mealTotalMainRating(meal), totalReviewCount))
                .amountRating(averageRating(mealTotalAmountRating(meal), totalReviewCount))
                .tasteRating(averageRating(mealTotalTasteRating(meal), totalReviewCount))
                .build();
    }

    // 메뉴 평균 평점 세트 계산
    public RatingAverages menuAverageRatings(Menu menu) {
        int totalReviewCount = menu.getTotalReviewCount();

        if (totalReviewCount == 0) {
            return new RatingAverages(null, null, null);
        }

        return RatingAverages.builder()
                .mainRating(averageRating(menuTotalMainRating(menu), totalReviewCount))
                .amountRating(averageRating(menuTotalAmountRating(menu), totalReviewCount))
                .tasteRating(averageRating(menuTotalTasteRating(menu), totalReviewCount))
                .build();
    }

    // 식단 메인 평점 평균 계산
    public Double mealAverageMainRating(Meal meal) {
        long totalReviewCount = mealTotalReviewCount(meal);

        if (totalReviewCount == 0) {
            return null;
        }

        return averageRating(mealTotalMainRating(meal), totalReviewCount);
    }

    // 메뉴 메인 평점 평균 계산
    public Double menuAverageMainRating(Menu menu) {
        if (menu.getReviews().size() == 0) {
            return null;
        }

        return averageRating(menuTotalMainRating(menu), menu.getReviews().size());
    }

    // 식단 총 리뷰 개수
    public long mealTotalReviewCount(Meal meal) {
        return meal.getMealMenus().stream()
                .map(MealMenu::getMenu)
                .mapToLong(menu -> menu.getReviews().size()).sum();
    }

    // 평균 평점 계산
    public Double averageRating(Integer totalRating, long totalReviewCount) {
        if (totalRating == null || totalReviewCount == 0) {
            return null;
        }
        return totalRating / (double) totalReviewCount;
    }

    // 식단 메인 평점 총합
    public Integer mealTotalMainRating(Meal meal) {
        return meal.getMealMenus().stream()
                .map(MealMenu::getMenu)
                .map(menu -> menu.getReviews().getTotalMainRating())
                .reduce(null, this::sum);
    }

    // 식단 양 평점 총합
    public Integer mealTotalAmountRating(Meal meal) {
        return meal.getMealMenus().stream()
                .map(MealMenu::getMenu)
                .map(menu -> menu.getReviews().getTotalAmountRating())
                .reduce(null, this::sum);
    }

    // 식단 맛 평점 총합
    public Integer mealTotalTasteRating(Meal meal) {
        return meal.getMealMenus().stream()
                .map(MealMenu::getMenu)
                .map(menu -> menu.getReviews().getTotalTasteRating())
                .reduce(null, this::sum);
    }

    // 메뉴 메인 평점 총합
    public Integer menuTotalMainRating(Menu menu) {
        return menu.getReviews().getTotalMainRating();
    }

    // 메뉴 양 평점 총합
    public Integer menuTotalAmountRating(Menu menu) {
        return menu.getReviews().getTotalAmountRating();
    }

    // 메뉴 맛 평점 총합
    public Integer menuTotalTasteRating(Menu menu) {
        return menu.getReviews().getTotalTasteRating();
    }

}