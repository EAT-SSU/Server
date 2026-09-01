package ssu.eatssu.domain.auth.entity;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import ssu.eatssu.domain.auth.dto.AppleKeys;
import ssu.eatssu.domain.auth.dto.OAuthInfo;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.math.BigInteger;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class SystemAppleAuthenticatorTest {

    private static final String KID = "test-kid";

    private RestTemplate restTemplate;
    private UserRepository userRepository;
    private SystemAppleAuthenticator authenticator;
    private KeyPair keyPair;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        restTemplate = mock(RestTemplate.class);
        userRepository = mock(UserRepository.class);
        authenticator = new SystemAppleAuthenticator(restTemplate, userRepository);

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();
    }

    @Test
    void getOAuthInfoReturnsEmailAndProviderIdFromFirstLoginToken() {
        mockAppleKeys("RSA", "RS256");
        String token = buildToken(builder -> builder.claim("sub", "apple-provider-id")
                                                     .claim("email", "user@icloud.com"));

        OAuthInfo info = authenticator.getOAuthInfoByIdentityToken(token);

        assertThat(info.providerId()).isEqualTo("apple-provider-id");
        assertThat(info.email()).isEqualTo("user@icloud.com");
    }

    @Test
    void getOAuthInfoLooksUpEmailByProviderIdWhenTokenHasNoEmail() {
        mockAppleKeys("RSA", "RS256");
        String token = buildToken(builder -> builder.claim("sub", "apple-provider-id"));
        User user = mock(User.class);
        given(user.getEmail()).willReturn("saved@icloud.com");
        given(userRepository.findByProviderId("apple-provider-id")).willReturn(Optional.of(user));

        OAuthInfo info = authenticator.getOAuthInfoByIdentityToken(token);

        assertThat(info.email()).isEqualTo("saved@icloud.com");
    }

    @Test
    void getOAuthInfoThrowsWhenReloginUserWasNeverRegistered() {
        mockAppleKeys("RSA", "RS256");
        String token = buildToken(builder -> builder.claim("sub", "apple-provider-id"));
        given(userRepository.findByProviderId("apple-provider-id")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authenticator.getOAuthInfoByIdentityToken(token))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void getOAuthInfoThrowsWhenProviderIdIsMissing() {
        mockAppleKeys("RSA", "RS256");
        String token = buildToken(builder -> builder.claim("email", "user@icloud.com"));

        assertThatThrownBy(() -> authenticator.getOAuthInfoByIdentityToken(token))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void getOAuthInfoThrowsWhenTokenIsExpired() {
        mockAppleKeys("RSA", "RS256");
        String token = buildToken(builder -> builder.claim("sub", "apple-provider-id")
                                                     .claim("email", "user@icloud.com")
                                                     .setExpiration(new Date(System.currentTimeMillis() - 60_000)));

        assertThatThrownBy(() -> authenticator.getOAuthInfoByIdentityToken(token))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void getOAuthInfoThrowsWhenNoCandidateKeyMatchesTokenHeader() {
        mockAppleKeys("RSA", "RS256", "other-kid");
        String token = buildToken(builder -> builder.claim("sub", "apple-provider-id"));

        assertThatThrownBy(() -> authenticator.getOAuthInfoByIdentityToken(token))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void getOAuthInfoThrowsWhenKeyAlgorithmIsUnsupported() {
        mockAppleKeys("UNSUPPORTED", "RS256");
        String token = buildToken(builder -> builder.claim("sub", "apple-provider-id"));

        assertThatThrownBy(() -> authenticator.getOAuthInfoByIdentityToken(token))
                .isInstanceOf(BaseException.class);
    }

    private String buildToken(java.util.function.UnaryOperator<io.jsonwebtoken.JwtBuilder> customizer) {
        io.jsonwebtoken.JwtBuilder builder = Jwts.builder()
                                                  .setHeaderParam("kid", KID)
                                                  .setHeaderParam("alg", "RS256");
        return customizer.apply(builder)
                         .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                         .compact();
    }

    private void mockAppleKeys(String kty, String alg) {
        mockAppleKeys(kty, alg, KID);
    }

    private void mockAppleKeys(String kty, String alg, String kid) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        AppleKeys.Key key = new AppleKeys.Key();
        ReflectionTestUtils.setField(key, "kty", kty);
        ReflectionTestUtils.setField(key, "kid", kid);
        ReflectionTestUtils.setField(key, "alg", alg);
        ReflectionTestUtils.setField(key, "n", encodeUnsigned(publicKey.getModulus()));
        ReflectionTestUtils.setField(key, "e", encodeUnsigned(publicKey.getPublicExponent()));

        AppleKeys appleKeys = new AppleKeys();
        ReflectionTestUtils.setField(appleKeys, "keys", List.of(key));

        given(restTemplate.getForEntity(any(URI.class), eq(AppleKeys.class)))
                .willReturn(ResponseEntity.ok(appleKeys));
    }

    private String encodeUnsigned(BigInteger value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray());
    }
}
