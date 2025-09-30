package ssu.eatssu.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.user.config.UserProperties;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;
import ssu.eatssu.domain.user.department.persistence.CollegeRepository;
import ssu.eatssu.domain.user.department.persistence.DepartmentRepository;
import ssu.eatssu.domain.user.dto.DepartmentResponse;
import ssu.eatssu.domain.user.dto.GetCollegeResponse;
import ssu.eatssu.domain.user.dto.GetDepartmentResponse;
import ssu.eatssu.domain.user.dto.MyPageResponse;
import ssu.eatssu.domain.user.dto.NicknameUpdateRequest;
import ssu.eatssu.domain.user.dto.UpdateDepartmentRequest;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.log.event.LogEvent;

import java.util.List;
import java.util.UUID;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.DUPLICATE_NICKNAME;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_DEPARTMENT;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.VALIDATION_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Component
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final UserProperties userProperties;
    private final CollegeRepository collegeRepository;
    private final ApplicationEventPublisher eventPublisher;

    public User join(String email, OAuthProvider provider, String providerId) {
        String credentials = createCredentials(provider, providerId);
        String nickname = createNickname();
        User user = User.create(email, nickname, provider, providerId, credentials);
        return userRepository.save(user);
    }

    public void updateNickname(CustomUserDetails userDetails, NicknameUpdateRequest request) {
        User user = userRepository.findById(userDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        if (isForbiddenNickname(request.nickname()) || userRepository.existsByNickname(request.nickname())) {
            throw new BaseException(DUPLICATE_NICKNAME);
        }

        user.updateNickname(request.nickname());

        eventPublisher.publishEvent(LogEvent.of(
                String.format("User nickname updated: userId=%d, newNickname=%s",
                        user.getId(), request.nickname()))
        );
    }

    public MyPageResponse findMyPage(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        return MyPageResponse.from(user);
    }

    public boolean withdraw(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        user.getReviews().forEach(Review::clearUser);
        user.getUserInquiries().forEach(inquiry -> inquiry.clearUser());
        userRepository.delete(user);

        eventPublisher.publishEvent(LogEvent.of(
                String.format("User withdrawn: userId=%d",
                        user.getId())
        ));

        return true;
    }

    public Boolean validateDuplicatedEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

    public Boolean validateDuplicatedNickname(String nickname) {
        if (isForbiddenNickname(nickname)) {
            return false;
        }
        return !userRepository.existsByNickname(nickname);
    }

    public String createNickname() {
        String uuid = UUID.randomUUID().toString();
        String shortUUID = uuid.substring(0, 4);
        return "user-" + shortUUID;
    }

    private String createCredentials(OAuthProvider provider, String providerId) {
        return passwordEncoder.encode(provider + providerId);
    }

    @Transactional
    public void registerDepartment(UpdateDepartmentRequest request, CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        Department department = departmentRepository.findById(request.getDepartmentId())
                                                    .orElseThrow(() -> new BaseException(NOT_FOUND_DEPARTMENT));

        user.updateDepartment(department);
    }

    public DepartmentResponse getDepartment(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        return DepartmentResponse.from(user.getDepartment());
    }

    public List<GetCollegeResponse> getCollegeList() {
        List<College> colleges = collegeRepository.findAll();
        return colleges.stream().map(college -> GetCollegeResponse.builder()
                                                                  .id(college.getId())
                                                                  .name(college.getName())
                                                                  .build())
                       .toList();
    }

    public List<GetDepartmentResponse> getDepartmentList(Long collegeId) {
        College college = collegeRepository.findById(collegeId)
                                           .orElseThrow(() -> new BaseException(VALIDATION_ERROR));
        List<Department> departments = departmentRepository.findByCollege(college);
        return departments.stream().map(department -> GetDepartmentResponse.builder()
                                                                           .id(department.getId())
                                                                           .name(department.getName())
                                                                           .build())
                          .toList();
    }

    private boolean isForbiddenNickname(String nickname) {
        return userProperties.getForbiddenNicknames().stream()
                             .anyMatch(forbidden -> forbidden.equalsIgnoreCase(nickname));
    }
}
