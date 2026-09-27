package ssu.eatssu.domain.notification.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssu.eatssu.domain.notification.entity.MenuFavoritePushHistory;

import java.time.LocalDate;
import java.util.Set;

public interface MenuFavoritePushHistoryRepository extends JpaRepository<MenuFavoritePushHistory, Long> {

    @Query("SELECT h.user.id FROM MenuFavoritePushHistory h WHERE h.pushDate = :pushDate")
    Set<Long> findUserIdsByPushDate(@Param("pushDate") LocalDate pushDate);
}
