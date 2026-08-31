package ssu.eatssu.global.runner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import ssu.eatssu.domain.auth.service.OAuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WarmUpRunnerTest {

    @Test
    void 첫_웜업_실패를_전파하지_않는다() throws Exception {
        OAuthService oauthService = mock(OAuthService.class);
        doThrow(new RuntimeException()).when(oauthService).kakaoLogin(any());

        new WarmUpRunner(oauthService).run(new DefaultApplicationArguments());

        verify(oauthService).kakaoLogin(any());
    }
}
