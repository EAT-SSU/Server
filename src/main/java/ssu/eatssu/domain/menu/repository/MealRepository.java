package ssu.eatssu.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.util.Date;
import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findAllByDateAndTimePartAndRestaurant(Date date, TimePart timePart,
                                                     Restaurant restaurant);
}
