package ssu.eatssu.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.menu.entity.MealMenu;

public interface MealMenuRepository extends JpaRepository<MealMenu, Long> {
}
