package ssu.eatssu.domain.partnership.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ssu.eatssu.domain.partnership.entity.Partnership;

public interface PartnershipRepository extends JpaRepository<Partnership, Long> {

}
