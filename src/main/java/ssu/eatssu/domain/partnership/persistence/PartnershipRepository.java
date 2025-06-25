package ssu.eatssu.domain.partnership.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;

import java.util.List;

public interface PartnershipRepository extends JpaRepository<Partnership, Long> {
    @Query("SELECT DISTINCT p FROM Partnership p " +
            "LEFT JOIN p.partnershipColleges pc " +
            "LEFT JOIN pc.college c " +
            "LEFT JOIN p.partnershipDepartments pd " +
            "LEFT JOIN pd.department d " +
            "WHERE (c = :college OR d = :department OR (c IS NOT NULL AND c.name = '총학'))")
    List<Partnership> findRelevantPartnerships(@Param("college") College college,
                                               @Param("department") Department department);
}
