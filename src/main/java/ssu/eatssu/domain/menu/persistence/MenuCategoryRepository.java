package ssu.eatssu.domain.menu.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.util.List;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

    List<MenuCategory> findAllByRestaurant(Restaurant restaurant);
}
