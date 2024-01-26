package ssu.eatssu.domain.user.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.user.dto.MyPageResponse;
import ssu.eatssu.domain.user.dto.NicknameUpdateRequest;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.global.handler.response.BaseException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Component
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public User join(String email, OAuthProvider provider, String providerId) {
        String credentials = createCredentials(provider, providerId);
        User user = User.create(email, provider, providerId, credentials);
        return userRepository.save(user);
    }

    public void updateNickname(CustomUserDetails userDetails, NicknameUpdateRequest request) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

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
        userRepository.delete(user);

        return true;
    }

    public Boolean validateDuplicatedEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

    public Boolean validateDuplicatedNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    private String createCredentials(OAuthProvider provider, String providerId) {
        return passwordEncoder.encode(provider + "_" + providerId);
    }
}