package ssu.eatssu.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.restaurant.Restaurant;
import ssu.eatssu.domain.restaurant.RestaurantName;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByRestaurantName(RestaurantName restaurantName);

}
