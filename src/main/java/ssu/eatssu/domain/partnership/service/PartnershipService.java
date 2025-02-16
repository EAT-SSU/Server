package ssu.eatssu.domain.partnership.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.INVALID_TARGET_TYPE;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_COLLEGE;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_DEPARTMENT;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.department.entity.College;
import ssu.eatssu.domain.department.entity.Department;
import ssu.eatssu.domain.department.persistence.CollegeRepository;
import ssu.eatssu.domain.department.persistence.DepartmentRepository;
import ssu.eatssu.domain.partnership.dto.CreatePartnershipRequest;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipCollege;
import ssu.eatssu.domain.partnership.entity.PartnershipDepartment;
import ssu.eatssu.domain.partnership.persistence.PartnershipRepository;
import ssu.eatssu.global.handler.response.BaseException;

@Service
@RequiredArgsConstructor
public class PartnershipService {
    private final PartnershipRepository partnershipRepository;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public void createPartnership(CreatePartnershipRequest request) {
        Partnership partnership = request.toPartnershipEntity();

        if ("college".equalsIgnoreCase(request.getTargetType())) {
            College college = collegeRepository.findByName(request.getTargetName())
                    .orElseThrow(() -> new BaseException(NOT_FOUND_COLLEGE));
            PartnershipCollege partnershipCollege = new PartnershipCollege(partnership, college);
            partnership.getPartnershipColleges().add(partnershipCollege);
        } else if ("department".equalsIgnoreCase(request.getTargetType())) {
            Department department = departmentRepository.findByName(request.getTargetName())
                    .orElseThrow(() -> new BaseException(NOT_FOUND_DEPARTMENT));
            PartnershipDepartment partnershipDepartment = new PartnershipDepartment(partnership, department);
            partnership.getPartnershipDepartments().add(partnershipDepartment);
        } else {
            throw new BaseException(INVALID_TARGET_TYPE);
        }
        partnershipRepository.save(partnership);
    }

}
