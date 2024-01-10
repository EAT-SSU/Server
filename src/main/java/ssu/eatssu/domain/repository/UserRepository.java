package ssu.eatssu.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByEmail(String email);

    Optional<User> findByProviderId(String providerId);
}
