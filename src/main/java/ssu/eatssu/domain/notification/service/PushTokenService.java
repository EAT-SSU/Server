package ssu.eatssu.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.notification.dto.request.DeletePushTokenRequest;
import ssu.eatssu.domain.notification.dto.request.PushTokenRequest;
import ssu.eatssu.domain.notification.entity.PushToken;
import ssu.eatssu.domain.notification.persistence.PushTokenRepository;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Transactional
public class PushTokenService {

    private final PushTokenRepository pushTokenRepository;
    private final UserRepository userRepository;

    public void registerToken(CustomUserDetails userDetails, PushTokenRequest request) {
        User user = userRepository.findById(userDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        pushTokenRepository.findByToken(request.token())
                           .ifPresentOrElse(
                                   pushToken -> pushToken.reassign(user, request.deviceType()),
                                   () -> pushTokenRepository.save(
                                           PushToken.of(user, request.token(), request.deviceType())));
    }

    public void deleteToken(CustomUserDetails userDetails, DeletePushTokenRequest request) {
        pushTokenRepository.deleteByUserIdAndToken(userDetails.getId(), request.token());
    }
}
