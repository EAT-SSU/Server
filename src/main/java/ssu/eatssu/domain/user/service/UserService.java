package ssu.eatssu.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.user.config.UserProperties;
import ssu.eatssu.domain.user.department.entity.Department;
import ssu.eatssu.domain.user.department.persistence.DepartmentRepository;
import ssu.eatssu.domain.user.dto.DepartmentResponse;
import ssu.eatssu.domain.user.dto.MyPageResponse;
import ssu.eatssu.domain.user.dto.NicknameUpdateRequest;
import ssu.eatssu.domain.user.dto.UpdateDepartmentRequest;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.UUID;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.DUPLICATE_NICKNAME;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.MISSING_USER_DEPARTMENT;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_DEPARTMENT;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;

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
    }

    public MyPageResponse findMyPage(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        return new MyPageResponse(user.getNickname(), user.getProvider());
    }

    public boolean withdraw(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        user.getReviews().forEach(Review::clearUser);
        user.getUserInquiries().forEach(inquiry -> inquiry.clearUser());
        userRepository.delete(user);

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
        Department department = departmentRepository.findByName(request.getDepartmentName())
                                                    .orElseThrow(() -> new BaseException(NOT_FOUND_DEPARTMENT));

        user.updateDepartment(department);
    }

    public Boolean validateDepartmentExists(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        return user.getDepartment() != null;
    }

    public DepartmentResponse getDepartment(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        Department department = user.getDepartment();
        if (department == null) {
            throw new BaseException(MISSING_USER_DEPARTMENT);
        }
        return new DepartmentResponse(department.getName());
    }

    private boolean isForbiddenNickname(String nickname) {
        return userProperties.getForbiddenNicknames().stream()
                             .anyMatch(forbidden -> forbidden.equalsIgnoreCase(nickname));
    }
}