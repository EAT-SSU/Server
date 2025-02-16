package ssu.eatssu.domain.department.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.department.entity.College;

public interface CollegeRepository extends JpaRepository<College, Long> {
    Optional<College> findByName(String name);
}
