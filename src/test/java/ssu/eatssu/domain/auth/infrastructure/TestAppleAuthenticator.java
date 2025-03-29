package ssu.eatssu.domain.auth.infrastructure;

import ssu.eatssu.domain.auth.dto.OAuthInfo;
import ssu.eatssu.domain.auth.entity.AppleAuthenticator;

public class TestAppleAuthenticator implements AppleAuthenticator {

	@Override
	public OAuthInfo getOAuthInfoByIdentityToken(String identityToken) {
		return new OAuthInfo("test@test.com", "1234567890");
	}
}
