package ssu.eatssu.domain.user.presentation;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.partnership.service.PartnershipService;
import ssu.eatssu.domain.review.service.ReviewServiceV2;
import ssu.eatssu.domain.slice.service.SliceService;
import ssu.eatssu.domain.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Test
    void 닉네임_사용_가능_여부를_반환한다() {
        UserService userService = mock(UserService.class);
        when(userService.validateNickname("잇슈")).thenReturn(true);
        UserController controller = new UserController(userService, mock(SliceService.class),
                                                       mock(PartnershipService.class), mock(ReviewServiceV2.class));

        assertThat(controller.validateNickname("잇슈").getResult()).isTrue();
    }
}
