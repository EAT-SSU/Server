package ssu.eatssu.domain.partnership.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssu.eatssu.domain.partnership.entity.PartnershipLike;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;
import ssu.eatssu.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface PartnershipLikeRepository extends JpaRepository<PartnershipLike, Long> {
    Optional<PartnershipLike> findByUserAndPartnershipRestaurant(User user,
                                                                 PartnershipRestaurant partnershipRestaurant);

    @Query("SELECT pl FROM PartnershipLike pl " +
            "JOIN FETCH pl.partnershipRestaurant pr " +
            "JOIN FETCH pr.partnerships p " +
            "LEFT JOIN FETCH p.partnershipCollege " +
            "LEFT JOIN FETCH p.partnershipDepartment " +
            "WHERE pl.user = :user")
    List<PartnershipLike> findAllByUserWithDetails(@Param("user") User user);
}
