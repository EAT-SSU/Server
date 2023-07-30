package ssu.eatssu.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.Restaurant;
import ssu.eatssu.domain.TodayMenu;
import ssu.eatssu.domain.enums.TimePart;

import java.util.Date;
import java.util.List;

public interface TodayMenuRepository extends JpaRepository<TodayMenu, Long> {
    List<TodayMenu> findAllByDateAndTimePartAndRestaurant(Date date, TimePart timePart, Restaurant restaurant);

    List<TodayMenu> findAllByDateAndTimePartAndRestaurantAndFlag(Date formatDate, TimePart timePart,
                                                                 Restaurant restaurant, Integer flag);
}
