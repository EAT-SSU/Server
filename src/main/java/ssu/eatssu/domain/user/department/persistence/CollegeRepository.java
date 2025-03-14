package ssu.eatssu.domain.user.department.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import ssu.eatssu.domain.user.department.entity.College;

import java.util.Optional;

public interface CollegeRepository extends JpaRepository<College, Long> {
	Optional<College> findByName(String name);
}
