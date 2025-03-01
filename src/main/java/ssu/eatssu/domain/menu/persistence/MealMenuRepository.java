package ssu.eatssu.domain.menu.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;

import java.util.List;

public interface MealMenuRepository extends JpaRepository<MealMenu, Long> {
    @Query("SELECT mm.menu FROM MealMenu mm WHERE mm.meal IN :meals")
    List<Menu> findMenusByMeals(List<Meal> meals);

    @Query("SELECT mm.menu.id FROM MealMenu mm WHERE mm.meal.id = :mealId")
    List<Long> findMenuIdsByMealId(@Param("mealId") Long mealId);

    @Query("SELECT DISTINCT mm.meal.id FROM MealMenu mm WHERE mm.menu.id IN :menuIds")
    List<Long> findMealIdsByMenuIds(@Param("menuIds") List<Long> menuIds);
}
