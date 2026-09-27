package ssu.eatssu.domain.notification.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.notification.entity.PushToken;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {

    Optional<PushToken> findByToken(String token);

    @Query("SELECT new ssu.eatssu.domain.notification.persistence.UserPushToken(t.user.id, t.token) "
            + "FROM PushToken t WHERE t.user.id IN :userIds")
    List<UserPushToken> findAllByUserIds(@Param("userIds") Collection<Long> userIds);

    @Modifying
    @Query("DELETE FROM PushToken t WHERE t.user.id = :userId AND t.token = :token")
    int deleteByUserIdAndToken(@Param("userId") Long userId, @Param("token") String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM PushToken t WHERE t.token IN :tokens")
    int deleteAllByTokens(@Param("tokens") Collection<String> tokens);
}
