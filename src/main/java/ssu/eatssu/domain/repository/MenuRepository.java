package ssu.eatssu.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.Menu;
import ssu.eatssu.domain.Restaurant;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    boolean existsByNameAndRestaurant(String name, Restaurant restaurant);

    Optional<Menu> findByNameAndRestaurant(String name, Restaurant restaurant);

    List<Menu> findAllByRestaurant(Restaurant restaurant);

    List<Menu> findAll();
}
