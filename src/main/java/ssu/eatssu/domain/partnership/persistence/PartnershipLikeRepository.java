package ssu.eatssu.domain.partnership.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssu.eatssu.domain.partnership.entity.PartnershipLike;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;
import ssu.eatssu.domain.user.entity.User;

import java.util.Optional;
import java.util.Set;

public interface PartnershipLikeRepository extends JpaRepository<PartnershipLike, Long> {
    Optional<PartnershipLike> findByUserAndPartnershipRestaurant(User user,
                                                                 PartnershipRestaurant partnershipRestaurant);

    @Query("SELECT pl.partnershipRestaurant.id FROM PartnershipLike pl WHERE pl.user = :user")
    Set<Long> findLikedRestaurantIdsByUser(@Param("user") User user);
}
