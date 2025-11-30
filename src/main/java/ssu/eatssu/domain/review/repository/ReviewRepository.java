package ssu.eatssu.domain.review.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.review.entity.Review;

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

    @Query("""
        SELECT r FROM Review r
        WHERE (r.meal.id = :mealId
            OR r.menu.id IN (SELECT mm.menu.id FROM MealMenu mm WHERE mm.meal.id = :mealId))
        """)
    List<Review> findAllMealAndMenuReviews(@Param("mealId") Long mealId);

    @Query("""
        SELECT r FROM Review r
        WHERE (r.meal.id = :mealId
            OR r.menu.id IN (SELECT mm.menu.id FROM MealMenu mm WHERE mm.meal.id = :mealId))
          AND (:lastReviewId IS NULL OR r.id < :lastReviewId)
        ORDER BY r.id DESC
        """)
    Page<Review> findMealAndMenuReviews(@Param("mealId") Long mealId,
        @Param("lastReviewId") Long lastReviewId,
        Pageable pageable);
}
