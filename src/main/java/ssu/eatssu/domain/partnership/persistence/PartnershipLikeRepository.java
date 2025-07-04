package ssu.eatssu.domain.partnership.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.partnership.entity.PartnershipLike;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;
import ssu.eatssu.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface PartnershipLikeRepository extends JpaRepository<PartnershipLike, Long> {
    Optional<PartnershipLike> findByUserAndPartnershipRestaurant(User user,
                                                                 PartnershipRestaurant partnershipRestaurant);

    List<PartnershipLike> findAllByUser(User user);
}
