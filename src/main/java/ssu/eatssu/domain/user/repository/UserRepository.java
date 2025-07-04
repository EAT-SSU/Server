package ssu.eatssu.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderId(String providerId);
}
