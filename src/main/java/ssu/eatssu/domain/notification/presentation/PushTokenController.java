package ssu.eatssu.domain.notification.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.notification.dto.request.DeletePushTokenRequest;
import ssu.eatssu.domain.notification.dto.request.PushTokenRequest;
import ssu.eatssu.domain.notification.presentation.docs.PushTokenControllerDocs;
import ssu.eatssu.domain.notification.service.PushTokenService;
import ssu.eatssu.global.handler.response.BaseResponse;

@RestController
@RequestMapping("/users/push-token")
@RequiredArgsConstructor
public class PushTokenController implements PushTokenControllerDocs {

    private final PushTokenService pushTokenService;

    @Override
    @PutMapping
    public BaseResponse<?> registerToken(@Valid @RequestBody PushTokenRequest request,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        pushTokenService.registerToken(userDetails, request);
        return BaseResponse.success();
    }

    @Override
    @DeleteMapping
    public BaseResponse<?> deleteToken(@Valid @RequestBody DeletePushTokenRequest request,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        pushTokenService.deleteToken(userDetails, request);
        return BaseResponse.success();
    }
}
