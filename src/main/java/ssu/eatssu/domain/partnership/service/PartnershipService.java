package ssu.eatssu.domain.partnership.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.INVALID_TARGET_TYPE;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.MISSING_USER_DEPARTMENT;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_COLLEGE;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_DEPARTMENT;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_PARTNERSHIP;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;
import ssu.eatssu.domain.user.department.persistence.CollegeRepository;
import ssu.eatssu.domain.user.department.persistence.DepartmentRepository;
import ssu.eatssu.domain.partnership.dto.CreatePartnershipRequest;
import ssu.eatssu.domain.partnership.dto.PartnershipDetailResponse;
import ssu.eatssu.domain.partnership.dto.PartnershipResponse;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipCollege;
import ssu.eatssu.domain.partnership.entity.PartnershipDepartment;
import ssu.eatssu.domain.partnership.entity.PartnershipLike;
import ssu.eatssu.domain.partnership.persistence.PartnershipLikeRepository;
import ssu.eatssu.domain.partnership.persistence.PartnershipRepository;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

@Service
@RequiredArgsConstructor
public class PartnershipService {
    private final PartnershipRepository partnershipRepository;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PartnershipLikeRepository partnershipLikeRepository;

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

    public List<PartnershipResponse> getAllPartnerships() {
        List<Partnership> partnerships = partnershipRepository.findAll();
        return partnerships.stream().map(PartnershipResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public PartnershipDetailResponse getPartnership(Long partnershipId, CustomUserDetails userDetails) {
        Partnership partnership = partnershipRepository.findById(partnershipId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_PARTNERSHIP));
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        boolean likedByUser = partnershipLikeRepository.findByUserAndPartnership(user, partnership).isPresent();

        return PartnershipDetailResponse.fromEntity(partnership, likedByUser);
    }

    @Transactional
    public void togglePartnershipLike(Long partnershipId, CustomUserDetails userDetails) {
        Partnership partnership = partnershipRepository.findById(partnershipId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_PARTNERSHIP));
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Optional<PartnershipLike> optionalPartnershipLike = partnershipLikeRepository.findByUserAndPartnership(user, partnership);
        if (optionalPartnershipLike.isPresent()) {
            PartnershipLike partnershipLike = optionalPartnershipLike.get();
            partnership.getLikes().remove(partnershipLike);
            partnershipLikeRepository.delete(partnershipLike);
        } else {
            PartnershipLike partnershipLike = new PartnershipLike(user, partnership);
            partnership.getLikes().add(partnershipLike);
            partnershipLikeRepository.save(partnershipLike);
        }
    }

    public List<PartnershipResponse> getUserLikedPartnerships(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        List<PartnershipLike> likes = partnershipLikeRepository.findAllByUser(user);
        return likes.stream().map(PartnershipLike::getPartnership)
                .map(PartnershipResponse::fromEntity).collect(Collectors.toList());
    }

    public List<PartnershipResponse> getUserDepartmentPartnerships(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Department department = user.getDepartment();
        if (department == null) {
            throw new BaseException(MISSING_USER_DEPARTMENT);
        }
        College college = department.getCollege();

        return partnershipRepository.findRelevantPartnerships(college, department).stream().map(PartnershipResponse::fromEntity).collect(Collectors.toList());
    }
}
