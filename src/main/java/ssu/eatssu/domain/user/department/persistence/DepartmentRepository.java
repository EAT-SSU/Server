package ssu.eatssu.domain.user.department.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByName(String name);

    Optional<Department> findById(Long id);

    List<Department> findByCollege(College college);
}
