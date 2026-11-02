package ssu.eatssu.domain.notification.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.notification.dto.response.NotificationResponse;
import ssu.eatssu.domain.notification.dto.response.UnreadNotificationResponse;
import ssu.eatssu.domain.notification.presentation.docs.NotificationControllerDocs;
import ssu.eatssu.domain.notification.service.NotificationService;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerDocs {

    private final NotificationService notificationService;

    @Override
    @GetMapping
    public BaseResponse<List<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(notificationService.getNotifications(userDetails));
    }

    @Override
    @GetMapping("/unread")
    public BaseResponse<UnreadNotificationResponse> hasUnread(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(notificationService.hasUnread(userDetails));
    }

    @Override
    @PatchMapping("/{notificationId}/read")
    public BaseResponse<?> read(@PathVariable Long notificationId,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.read(userDetails, notificationId);
        return BaseResponse.success();
    }

    @Override
    @PatchMapping("/read-all")
    public BaseResponse<?> readAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.readAll(userDetails);
        return BaseResponse.success();
    }
}
