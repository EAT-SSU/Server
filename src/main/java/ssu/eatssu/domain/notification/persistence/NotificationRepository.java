package ssu.eatssu.domain.notification.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssu.eatssu.domain.notification.entity.Notification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT DISTINCT n FROM Notification n LEFT JOIN FETCH n.menus "
            + "WHERE n.user.id = :userId AND n.targetDate >= :fromDate "
            + "ORDER BY n.targetDate DESC, n.id DESC")
    List<Notification> findRecentWithMenus(@Param("userId") Long userId, @Param("fromDate") LocalDate fromDate);

    @Query("SELECT COUNT(n) > 0 FROM Notification n "
            + "WHERE n.user.id = :userId AND n.isRead = false AND n.targetDate >= :fromDate")
    boolean existsUnread(@Param("userId") Long userId, @Param("fromDate") LocalDate fromDate);

    @Query("SELECT n FROM Notification n WHERE n.id = :notificationId AND n.user.id = :userId")
    Optional<Notification> findByIdAndUserId(@Param("notificationId") Long notificationId,
                                             @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllRead(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.targetDate < :date")
    int deleteAllByTargetDateBefore(@Param("date") LocalDate date);
}
