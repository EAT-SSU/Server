package ssu.eatssu.domain.admin.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.menu.entity.MealMenu;

public interface ManageMealMenuRepository extends JpaRepository<MealMenu, Long> {
}
