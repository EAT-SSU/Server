package ssu.eatssu.domain.partnership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.partnership.dto.CreatePartnershipRequest;
import ssu.eatssu.domain.partnership.dto.PartnershipResponse;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipLike;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;
import ssu.eatssu.domain.partnership.persistence.PartnershipLikeRepository;
import ssu.eatssu.domain.partnership.persistence.PartnershipRepository;
import ssu.eatssu.domain.partnership.persistence.PartnershipRestaurantRepository;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;
import ssu.eatssu.domain.user.department.persistence.CollegeRepository;
import ssu.eatssu.domain.user.department.persistence.DepartmentRepository;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.MISSING_USER_DEPARTMENT;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_COLLEGE;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_DEPARTMENT;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_PARTNERSHIP;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_PARTNERSHIP_RESTAURANT;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class PartnershipService {
    private final PartnershipRepository partnershipRepository;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PartnershipLikeRepository partnershipLikeRepository;
    private final PartnershipRestaurantRepository partnerShipRestaurantRepository;

    @Transactional
    public void createPartnership(CreatePartnershipRequest request) {
        PartnershipRestaurant partnershipRestaurant = partnerShipRestaurantRepository.findById(request.getStoreId())
                                                                                     .orElseThrow(() -> new BaseException(
                                                                                             NOT_FOUND_PARTNERSHIP_RESTAURANT));
        Partnership partnership = request.toPartnershipEntity(partnershipRestaurant);

        College college = collegeRepository.findByName(request.getCollege())
                                           .orElseThrow(() -> new BaseException(NOT_FOUND_COLLEGE));
        partnership.setPartnershipCollege(college);
        Department department = departmentRepository.findByName(request.getDepartment())
                                                    .orElseThrow(() -> new BaseException(NOT_FOUND_DEPARTMENT));
        partnership.setPartnershipDepartment(department);
        partnershipRepository.save(partnership);
    }

    public List<PartnershipResponse> getAllPartnerships(CustomUserDetails customUserDetails) {
        return partnerShipRestaurantRepository.findAllWithDetails().stream()
                                              .map(restaurant -> PartnershipResponse.fromEntity(restaurant,
                                                                                                customUserDetails.getId()))
                                              .collect(Collectors.toList());
    }


    @Transactional
    public void togglePartnershipLike(Long partnershipId, CustomUserDetails userDetails) {
        Partnership partnership = partnershipRepository.findById(partnershipId)
                                                       .orElseThrow(() -> new BaseException(NOT_FOUND_PARTNERSHIP));
        User user = userRepository.findById(userDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        PartnershipRestaurant partnershipRestaurant = partnership.getPartnershipRestaurant();

        Optional<PartnershipLike> optionalPartnershipLike = partnershipLikeRepository.findByUserAndPartnershipRestaurant(
                user,
                partnershipRestaurant);
        if (optionalPartnershipLike.isPresent()) {
            PartnershipLike partnershipLike = optionalPartnershipLike.get();
            partnershipRestaurant.getLikes().remove(partnershipLike);
            partnershipLikeRepository.delete(partnershipLike);
        } else {
            PartnershipLike partnershipLike = new PartnershipLike(user, partnershipRestaurant);
            partnershipRestaurant.getLikes().add(partnershipLike);
            partnershipLikeRepository.save(partnershipLike);
        }
    }

    public List<PartnershipResponse> getUserLikedPartnerships(CustomUserDetails customUserDetails) {
        User user = userRepository.findById(customUserDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        List<PartnershipLike> likes = partnershipLikeRepository.findAllByUserWithDetails(user);

        return likes.stream()
                    .flatMap(like -> {
                        PartnershipRestaurant restaurant = like.getPartnershipRestaurant();
                        return restaurant.getPartnerships()
                                         .stream()
                                         .map(partnership -> PartnershipResponse.fromEntity(restaurant,
                                                                                            customUserDetails.getId()));
                    }).collect(Collectors.toList());
    }


    public List<PartnershipResponse> getUserDepartmentPartnerships(CustomUserDetails customUserDetails) {
        User user = userRepository.findById(customUserDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Department department = user.getDepartment();
        if (department == null) {
            throw new BaseException(MISSING_USER_DEPARTMENT);
        }
        College college = department.getCollege();

        return partnershipRepository
                .findRestaurantsWithMyPartnerships(college, department)
                .stream()
                .map(partnership -> PartnershipResponse.fromEntity(partnership,
                                                               customUserDetails.getId()))
                .collect(Collectors.toList());
    }
}
