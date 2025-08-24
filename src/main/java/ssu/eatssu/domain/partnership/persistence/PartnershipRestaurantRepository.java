package ssu.eatssu.domain.partnership.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;

import java.util.List;

public interface PartnershipRestaurantRepository extends JpaRepository<PartnershipRestaurant, Long> {
    @Query("SELECT DISTINCT pr FROM PartnershipRestaurant pr " +
            "LEFT JOIN FETCH pr.partnerships p " +
            "LEFT JOIN FETCH p.partnershipCollege " +
            "LEFT JOIN FETCH p.partnershipDepartment " +
            "WHERE CURRENT_DATE BETWEEN p.startDate AND p.endDate")
    List<PartnershipRestaurant> findAllWithDetails();
}
