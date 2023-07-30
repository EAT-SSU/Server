package ssu.eatssu.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.MealMenu;

public interface MealMenuRepository extends JpaRepository<MealMenu, Long> {
}
