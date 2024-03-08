package ssu.eatssu.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    boolean existsByNameAndRestaurant(String name, Restaurant restaurant);

    Optional<Menu> findByNameAndRestaurant(String name, Restaurant restaurant);

    List<Menu> findAllByRestaurantAndCategory(Restaurant restaurant, MenuCategory category);

    List<Menu> findAll();
}
