package ssu.eatssu.domain.menu.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.menu.entity.MealMainMenu;

import java.util.List;

public interface MealMainMenuRepository extends JpaRepository<MealMainMenu, Long> {

    List<MealMainMenu> findAllByMeal_Id(Long mealId);

    void deleteAllByMeal_Id(Long mealId);
}
