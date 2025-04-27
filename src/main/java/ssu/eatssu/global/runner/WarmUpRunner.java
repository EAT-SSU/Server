package ssu.eatssu.global.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.auth.dto.AppleLoginRequest;
import ssu.eatssu.domain.auth.dto.KakaoLoginRequest;
import ssu.eatssu.domain.auth.service.OAuthService;

@Component
@RequiredArgsConstructor
public class WarmUpRunner implements ApplicationRunner {

	private final OAuthService oAuthService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try{
			KakaoLoginRequest kakaoRequest = new KakaoLoginRequest("","");
			oAuthService.kakaoLogin(kakaoRequest);

			AppleLoginRequest appleLoginRequest = new AppleLoginRequest("");
			oAuthService.appleLogin(appleLoginRequest);

		}catch (Exception e){

		}

	}
}
