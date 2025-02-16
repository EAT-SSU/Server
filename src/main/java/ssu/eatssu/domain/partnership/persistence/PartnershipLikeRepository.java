package ssu.eatssu.domain.partnership.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipLike;
import ssu.eatssu.domain.user.entity.User;

public interface PartnershipLikeRepository extends JpaRepository<PartnershipLike, Long> {
    Optional<PartnershipLike> findByUserAndPartnership(User user, Partnership partnership);
}
