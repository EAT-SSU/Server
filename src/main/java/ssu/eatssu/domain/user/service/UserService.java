package ssu.eatssu.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.auth.util.RandomNicknameUtil;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.user.department.entity.College;
import ssu.eatssu.domain.user.department.entity.Department;
import ssu.eatssu.domain.user.department.persistence.CollegeRepository;
import ssu.eatssu.domain.user.department.persistence.DepartmentRepository;
import ssu.eatssu.domain.user.dto.response.DepartmentResponse;
import ssu.eatssu.domain.user.dto.response.GetCollegeResponse;
import ssu.eatssu.domain.user.dto.response.GetDepartmentResponse;
import ssu.eatssu.domain.user.dto.response.LanguageResponse;
import ssu.eatssu.domain.user.dto.request.LanguageUpdateRequest;
import ssu.eatssu.domain.user.dto.response.MyPageResponse;
import ssu.eatssu.domain.user.dto.request.NicknameUpdateRequest;
import ssu.eatssu.domain.user.dto.request.UpdateDepartmentRequest;
import ssu.eatssu.domain.user.entity.DeviceType;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.domain.user.util.NicknameValidator;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.log.event.LogEvent;

import java.util.List;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.DUPLICATE_NICKNAME;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_DEPARTMENT;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.VALIDATION_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final CollegeRepository collegeRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RandomNicknameUtil randomNicknameUtil;
    private final NicknameValidator nicknameValidator;

    public User join(String email, OAuthProvider provider, String providerId) {
        String credentials = createCredentials(provider, providerId);
        String nickname = randomNicknameUtil.generate();
        User user = User.create(email, nickname, provider, providerId, credentials);
        return userRepository.save(user);
    }

    public User joinV2(String email, OAuthProvider provider, String providerId, DeviceType deviceType) {
        String credentials = createCredentials(provider, providerId);
        String nickname = randomNicknameUtil.generate();
        User user = User.createV2(email, nickname, provider, providerId, credentials, deviceType);
        return userRepository.save(user);
    }

    public void updateNickname(CustomUserDetails userDetails, NicknameUpdateRequest request) {
        User user = findUserByUserDetails(userDetails);

        nicknameValidator.validateNickname(request.nickname());

        // 닉네임 생성 직전 다른 사람이 생성한 경우.
        if (userRepository.existsByNickname(request.nickname())) {
            throw new BaseException(DUPLICATE_NICKNAME);
        }

        user.updateNickname(request.nickname());

        eventPublisher.publishEvent(LogEvent.of(
                String.format("User nickname updated: userId=%d, newNickname=%s",
                        user.getId(), request.nickname()))
        );
    }

    public MyPageResponse findMyPage(CustomUserDetails userDetails) {
        User user = findUserByUserDetails(userDetails);
        return MyPageResponse.from(user);
    }

    public void updateLanguage(CustomUserDetails userDetails, LanguageUpdateRequest request) {
        User user = findUserByUserDetails(userDetails);

        user.updateLanguage(request.language());

        eventPublisher.publishEvent(LogEvent.of(
                String.format("User language updated: userId=%d, language=%s",
                        user.getId(), request.language()))
        );
    }

    public LanguageResponse findLanguage(CustomUserDetails userDetails) {
        User user = findUserByUserDetails(userDetails);
        return LanguageResponse.from(user);
    }

    public boolean withdraw(CustomUserDetails userDetails) {
        User user = findUserByUserDetails(userDetails);

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

    public Boolean validateNickname(String nickname) {

        nicknameValidator.validateNickname(nickname);

        return !userRepository.existsByNickname(nickname);
    }

    private String createCredentials(OAuthProvider provider, String providerId) {
        return passwordEncoder.encode(provider + providerId);
    }

    @Transactional
    public void registerDepartment(UpdateDepartmentRequest request, CustomUserDetails userDetails) {
        User user = findUserByUserDetails(userDetails);
        Department department = departmentRepository.findById(request.getDepartmentId())
                                                    .orElseThrow(() -> new BaseException(NOT_FOUND_DEPARTMENT));

        user.updateDepartment(department);
    }

    public DepartmentResponse getDepartment(CustomUserDetails userDetails) {
        User user = findUserByUserDetails(userDetails);
        return DepartmentResponse.from(user.getDepartment(), user.getLanguage());
    }

    private User findUserByUserDetails(CustomUserDetails userDetails) {
        return userRepository.findById(userDetails.getId())
                             .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
    }

    public List<GetCollegeResponse> getCollegeList(CustomUserDetails userDetails) {
        Language language = findLanguageOrDefault(userDetails);
        List<College> colleges = collegeRepository.findAll();
        return colleges.stream().map(college -> GetCollegeResponse.builder()
                                                                  .id(college.getId())
                                                                  .name(college.getNameByLanguage(language))
                                                                  .build())
                       .toList();
    }

    public List<GetDepartmentResponse> getDepartmentList(Long collegeId, CustomUserDetails userDetails) {
        Language language = findLanguageOrDefault(userDetails);
        College college = collegeRepository.findById(collegeId)
                                           .orElseThrow(() -> new BaseException(VALIDATION_ERROR));
        List<Department> departments = departmentRepository.findByCollege(college);
        return departments.stream().map(department -> GetDepartmentResponse.builder()
                                                                           .id(department.getId())
                                                                           .name(department.getNameByLanguage(language))
                                                                           .build())
                          .toList();
    }

    private Language findLanguageOrDefault(CustomUserDetails userDetails) {
        if (userDetails == null) {
            return Language.KO;
        }
        return findUserByUserDetails(userDetails).getLanguage();
    }
}
