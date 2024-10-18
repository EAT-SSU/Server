package ssu.eatssu.domain.menu.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

    List<MenuCategory> findAllByRestaurant(Restaurant restaurant);
}
