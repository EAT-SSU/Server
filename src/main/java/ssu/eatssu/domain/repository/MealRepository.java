package ssu.eatssu.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.Meal;
import ssu.eatssu.domain.Restaurant;
import ssu.eatssu.domain.enums.TimePart;

import java.util.Date;
import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findAllByDateAndTimePartAndRestaurant(Date date, TimePart timePart, Restaurant restaurant);
}
