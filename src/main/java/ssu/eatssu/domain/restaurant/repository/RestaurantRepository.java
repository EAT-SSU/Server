package ssu.eatssu.domain.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.restaurant.entity.RestaurantName;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByRestaurantName(RestaurantName restaurantName);

}
