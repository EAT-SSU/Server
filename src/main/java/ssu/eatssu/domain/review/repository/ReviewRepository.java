package ssu.eatssu.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.review.entity.Review;

import java.util.Collection;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    List<Review> findAllByMenu(Menu menu);

    /**
     * dto projection
     */
    <T> Collection<T> findByMenu_MealMenus_Meal(Meal meal, Class<T> type);

    Long countByMenu_MealMenus_Meal(Meal meal);

}
