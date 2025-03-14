package ssu.eatssu.domain.admin.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ssu.eatssu.domain.menu.entity.Menu;

import java.util.Optional;

@Repository
public interface ManageMenuRepository extends JpaRepository<Menu, Long> {
	Optional<Menu> findById(Long id);
}
