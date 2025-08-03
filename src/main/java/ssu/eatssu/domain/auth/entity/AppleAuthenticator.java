package ssu.eatssu.domain.auth.entity;

import ssu.eatssu.domain.auth.dto.OAuthInfo;

public interface AppleAuthenticator {

    OAuthInfo getOAuthInfoByIdentityToken(String identityToken);
}
