package ssu.eatssu.domain.user.department.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ssu.eatssu.domain.user.department.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
	Optional<Department> findByName(String name);
}
