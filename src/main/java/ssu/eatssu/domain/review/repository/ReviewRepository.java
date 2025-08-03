package ssu.eatssu.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.review.entity.Review;

import java.util.Collection;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    List<Review> findAllByMenu(Menu menu);

    List<Review> findAllByMeal(Meal meal);

    /**
     * dto projection
     */
    <T> Collection<T> findByMenu_MealMenus_Meal(Meal meal, Class<T> type);

    Long countByMenu_MealMenus_Meal(Meal meal);

    List<Review> findByMealIn(List<Meal> meals);

    @Query("SELECT r FROM Review r WHERE r.meal.id IN :mealIds AND (:lastReviewId IS NULL OR r.id < :lastReviewId) ")
    Page<Review> findReviewsByMealIds(@Param("mealIds") List<Long> mealIds,
                                      @Param("lastReviewId") Long lastReviewId,
                                      Pageable pageable);
}
