package ssu.eatssu.domain.partnership.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;

import java.util.Optional;

public interface PartnerShipRestaurantRepository extends JpaRepository<PartnershipRestaurant, Long> {
}
