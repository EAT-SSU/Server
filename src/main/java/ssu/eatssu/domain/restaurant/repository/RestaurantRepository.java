package ssu.eatssu.domain.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.restaurant.entity.TemporalRestaurant;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<TemporalRestaurant, Long> {

    Optional<TemporalRestaurant> findByRestaurantName(Restaurant restaurantName);

}
