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
            "LEFT JOIN p.partnershipCollege pc " +
            "LEFT JOIN p.partnershipDepartment pd " +
            "WHERE (pc = :college OR pd = :department OR (pc IS NOT NULL AND pc.name = '총학'))")
    List<Partnership> findRelevantPartnerships(@Param("college") College college,
                                               @Param("department") Department department);
}
