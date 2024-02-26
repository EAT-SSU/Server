package ssu.eatssu.domain.admin.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.menu.entity.Meal;

public interface ManageMealRepository extends JpaRepository<Meal, Long> {
}
