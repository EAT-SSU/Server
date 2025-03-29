package ssu.eatssu.domain.menu.persistence;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

public interface MealRepository extends JpaRepository<Meal, Long> {

	List<Meal> findAllByDateAndTimePartAndRestaurant(Date date, TimePart timePart, Restaurant restaurant);

	List<Meal> findByRestaurant(Restaurant restaurant);
}
