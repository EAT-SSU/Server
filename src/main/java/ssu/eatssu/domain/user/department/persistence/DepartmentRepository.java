package ssu.eatssu.domain.user.department.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.user.department.entity.Department;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByName(String name);
}
