package ssu.eatssu.domain.admin.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ssu.eatssu.domain.menu.entity.MenuCategory;

@Repository
public interface ManageCategoryRepository extends JpaRepository<MenuCategory, Long> {

}
