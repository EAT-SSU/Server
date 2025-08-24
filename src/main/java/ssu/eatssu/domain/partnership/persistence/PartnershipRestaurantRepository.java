package ssu.eatssu.domain.partnership.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;

import java.util.List;

public interface PartnershipRestaurantRepository extends JpaRepository<PartnershipRestaurant, Long> {
    @Query("SELECT DISTINCT pr FROM PartnershipRestaurant pr " +
            "LEFT JOIN FETCH pr.partnerships p " +
            "LEFT JOIN FETCH p.partnershipCollege " +
            "LEFT JOIN FETCH p.partnershipDepartment "+
            "WHERE p.startDate <= CURRENT_DATE and (p.endDate is null or p.endDate >= CURRENT_DATE)")
    List<PartnershipRestaurant> findAllWithDetails();
}
