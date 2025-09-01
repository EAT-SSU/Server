package ssu.eatssu.domain.partnership.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;

import java.util.List;

public interface PartnershipRepository extends JpaRepository<Partnership, Long> {
    @Query("""
            select distinct pr
            from PartnershipRestaurant pr
            join fetch pr.partnerships p
            left join fetch p.partnershipCollege pc
            left join fetch p.partnershipDepartment pd
            where
              (pc = :college
               or pd = :department
               or (pc is not null and pc.name = '총학'))
              and p.startDate <= current_date
              and (p.endDate is null or p.endDate >= current_date)
            """)
    List<PartnershipRestaurant> findRestaurantsWithMyPartnerships(
            @Param("college") College college,
            @Param("department") Department department
                                                                 );

}
