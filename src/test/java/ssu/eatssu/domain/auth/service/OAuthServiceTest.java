package ssu.eatssu.domain.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import ssu.eatssu.domain.auth.dto.AppleLoginRequest;
import ssu.eatssu.domain.auth.dto.KakaoLoginRequest;
import ssu.eatssu.domain.auth.dto.OAuthInfo;
import ssu.eatssu.domain.auth.dto.ValidRequest;
import ssu.eatssu.domain.auth.entity.AppleAuthenticator;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.user.dto.Tokens;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.entity.UserStatus;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.domain.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OAuthServiceTest {

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private AppleAuthenticator appleAuthenticator;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private String createMockAppleIdentityToken(String email, String providerId) {
        try {
            SecretKey key = KeyGenerator.getInstance("HmacSHA256").generateKey();
            
            Map<String, Object> claims = new HashMap<>();
            claims.put("iss", "https://appleid.apple.com");
            claims.put("aud", "com.example.testapp");
            claims.put("exp", Instant.now().plusSeconds(3600).getEpochSecond());
            claims.put("iat", Instant.now().getEpochSecond());
            claims.put("sub", providerId);
            claims.put("email", email);
            claims.put("email_verified", "true");
            
            Map<String, Object> header = new HashMap<>();
            header.put("kid", "test-key-id");
            header.put("alg", "HS256");
            
            return Jwts.builder()
                    .setHeader(header)
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("모의 Apple Identity Token 생성에 실패했습니다", e);
        }
    }

    @Test
    @DisplayName("카카오 로그인 - 신규 사용자 가입 및 토큰 생성")
    void kakaoLogin_신규사용자_토큰생성() {
        // given
        String email = "test@kakao.com";
        String providerId = "kakao123";
        KakaoLoginRequest request = new KakaoLoginRequest(email, providerId);

        // when
        Tokens tokens = oAuthService.kakaoLogin(request);

        // then
        assertThat(tokens).isNotNull();
        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank();
        
        User savedUser = userRepository.findByProviderId(providerId).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getProvider()).isEqualTo(OAuthProvider.KAKAO);
        assertThat(savedUser.getProviderId()).isEqualTo(providerId);
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
        assertThat(savedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("카카오 로그인 - 기존 사용자 로그인")
    void kakaoLogin_기존사용자_로그인() {
        // given
        String email = "existing@kakao.com";
        String providerId = "existing123";
        User existingUser = userService.join(email, OAuthProvider.KAKAO, providerId);
        existingUser.updateNickname("테스트닉네임");
        userRepository.save(existingUser);
        
        KakaoLoginRequest request = new KakaoLoginRequest(email, providerId);

        // when
        Tokens tokens = oAuthService.kakaoLogin(request);

        // then
        assertThat(tokens).isNotNull();
        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank();
        
        long userCount = userRepository.count();
        assertThat(userCount).isEqualTo(1);
    }

    @Test
    @DisplayName("애플 로그인 - 신규 사용자 가입 및 토큰 생성")
    void appleLogin_신규사용자_토큰생성() {
        // given
        String email = "test@apple.com";
        String providerId = "apple123";
        String identityToken = "mock.identity.token";
        AppleLoginRequest request = new AppleLoginRequest(identityToken);
        
        OAuthInfo mockOAuthInfo = new OAuthInfo(email, providerId);
        given(appleAuthenticator.getOAuthInfoByIdentityToken(identityToken)).willReturn(mockOAuthInfo);

        // when
        Tokens tokens = oAuthService.appleLogin(request);

        // then
        assertThat(tokens).isNotNull();
        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank();
        
        User savedUser = userRepository.findByProviderId(providerId).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getProvider()).isEqualTo(OAuthProvider.APPLE);
        assertThat(savedUser.getProviderId()).isEqualTo(providerId);
    }

    @Test
    @DisplayName("애플 로그인 - 기존 사용자 로그인")
    void appleLogin_기존사용자_로그인() {
        // given
        String email = "existing@apple.com";
        String providerId = "existing_apple123";
        String identityToken = "mock.identity.token";
        
        User existingUser = userService.join(email, OAuthProvider.APPLE, providerId);
        existingUser.updateNickname("기존애플사용자");
        userRepository.save(existingUser);
        
        AppleLoginRequest request = new AppleLoginRequest(identityToken);
        OAuthInfo mockOAuthInfo = new OAuthInfo(email, providerId);
        given(appleAuthenticator.getOAuthInfoByIdentityToken(identityToken)).willReturn(mockOAuthInfo);

        // when
        Tokens tokens = oAuthService.appleLogin(request);

        // then
        assertThat(tokens).isNotNull();
        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank();
        
        long userCount = userRepository.count();
        assertThat(userCount).isEqualTo(1);
    }

    @Test
    @DisplayName("애플 로그인 - 프라이빗 릴레이 이메일에서 실제 이메일로 업데이트")
    void appleLogin_프라이빗릴레이이메일_실제이메일업데이트() {
        // given
        String privateEmail = "abc123@privaterelay.appleid.com";
        String realEmail = "user@icloud.com";
        String providerId = "apple_private123";
        String identityToken = "mock.identity.token";
        
        User existingUser = userService.join(privateEmail, OAuthProvider.APPLE, providerId);
        existingUser.updateNickname("프라이빗사용자");
        userRepository.save(existingUser);
        
        AppleLoginRequest request = new AppleLoginRequest(identityToken);
        OAuthInfo mockOAuthInfo = new OAuthInfo(realEmail, providerId);
        given(appleAuthenticator.getOAuthInfoByIdentityToken(identityToken)).willReturn(mockOAuthInfo);

        // when
        Tokens tokens = oAuthService.appleLogin(request);

        // then
        assertThat(tokens).isNotNull();
        
        User updatedUser = userRepository.findByProviderId(providerId).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo(realEmail);
    }

    @Test
    @DisplayName("애플 로그인 - 실제 이메일에서 프라이빗 릴레이 이메일로 변경 시 업데이트 안됨")
    void appleLogin_실제이메일_프라이빗릴레이이메일_업데이트안됨() {
        // given
        String realEmail = "user@icloud.com";
        String privateEmail = "xyz789@privaterelay.appleid.com";
        String providerId = "apple_real123";
        String identityToken = "mock.identity.token";
        
        User existingUser = userService.join(realEmail, OAuthProvider.APPLE, providerId);
        existingUser.updateNickname("실제사용자");
        userRepository.save(existingUser);
        
        AppleLoginRequest request = new AppleLoginRequest(identityToken);
        OAuthInfo mockOAuthInfo = new OAuthInfo(privateEmail, providerId);
        given(appleAuthenticator.getOAuthInfoByIdentityToken(identityToken)).willReturn(mockOAuthInfo);

        // when
        Tokens tokens = oAuthService.appleLogin(request);

        // then
        assertThat(tokens).isNotNull();
        
        User updatedUser = userRepository.findByProviderId(providerId).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo(realEmail);
    }

    @Test
    @DisplayName("토큰 새로고침 - 유효한 Authentication으로 새 토큰 생성")
    void refreshTokens_유효한인증_새토큰생성() {
        // given
        String email = "refresh@test.com";
        String providerId = "refresh123";
        KakaoLoginRequest request = new KakaoLoginRequest(email, providerId);
        Tokens originalTokens = oAuthService.kakaoLogin(request);
        
        User user = userRepository.findByProviderId(providerId).orElseThrow();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, 
            userDetails.getCredentials(), 
            userDetails.getAuthorities()
        );

        // when
        Tokens refreshedTokens = oAuthService.refreshTokens(authentication);

        // then
        assertThat(refreshedTokens).isNotNull();
        assertThat(refreshedTokens.accessToken()).isNotBlank();
        assertThat(refreshedTokens.refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("유효한 토큰 검증 - 유효한 토큰")
    void validToken_유효한토큰_true반환() {
        // given
        String email = "valid@test.com";
        String providerId = "valid123";
        KakaoLoginRequest request = new KakaoLoginRequest(email, providerId);
        Tokens tokens = oAuthService.kakaoLogin(request);
        
        ValidRequest validRequest = new ValidRequest(tokens.accessToken());

        // when
        Boolean result = oAuthService.validToken(validRequest);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 토큰 검증 - 무효한 토큰")
    void validToken_무효한토큰_false반환() {
        // given
        String invalidToken = "invalid.jwt.token";
        ValidRequest request = new ValidRequest(invalidToken);

        // when
        Boolean result = oAuthService.validToken(request);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("토큰 검증 - 빈 토큰")
    void validToken_빈토큰_false반환() {
        // given
        String emptyToken = "";
        ValidRequest request = new ValidRequest(emptyToken);

        // when
        Boolean result = oAuthService.validToken(request);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("토큰 검증 - null 토큰")
    void validToken_null토큰_false반환() {
        // given
        ValidRequest request = new ValidRequest(null);

        // when
        Boolean result = oAuthService.validToken(request);

        // then
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @DisplayName("다양한 providerId로 카카오 로그인 테스트")
    @ValueSource(strings = {"kakao123", "kakao_test_456", "1234567890"})
    void kakaoLogin_다양한providerId_성공(String providerId) {
        // given
        String email = "test@example.com";
        KakaoLoginRequest request = new KakaoLoginRequest(email, providerId);

        // when
        Tokens tokens = oAuthService.kakaoLogin(request);

        // then
        assertThat(tokens).isNotNull();
        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank();
        
        User savedUser = userRepository.findByProviderId(providerId).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getProviderId()).isEqualTo(providerId);
        assertThat(savedUser.getProvider()).isEqualTo(OAuthProvider.KAKAO);
    }

    @Test
    @DisplayName("동일한 providerId로 중복 로그인 시도")
    void kakaoLogin_중복providerId_기존사용자반환() {
        // given
        String email = "first@kakao.com";
        String providerId = "duplicate123";
        
        KakaoLoginRequest firstRequest = new KakaoLoginRequest(email, providerId);
        oAuthService.kakaoLogin(firstRequest);
        
        KakaoLoginRequest secondRequest = new KakaoLoginRequest("different@email.com", providerId);

        // when
        Tokens secondTokens = oAuthService.kakaoLogin(secondRequest);

        // then
        assertThat(secondTokens).isNotNull();
        long userCount = userRepository.count();
        assertThat(userCount).isEqualTo(1);
        
        User user = userRepository.findByProviderId(providerId).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(email);
    }

    @ParameterizedTest
    @DisplayName("프라이빗 릴레이 이메일 패턴 테스트")
    @ValueSource(strings = {
            "test123@privaterelay.appleid.com",
            "a1b2c3d4e5f6@privaterelay.appleid.com",
            "verylongusername@privaterelay.appleid.com"
    })
    void appleLogin_프라이빗릴레이이메일패턴_인식확인(String privateEmail) {
        // given
        String providerId = "apple_private_test";
        String identityToken = "mock.identity.token";
        
        AppleLoginRequest request = new AppleLoginRequest(identityToken);
        OAuthInfo mockOAuthInfo = new OAuthInfo(privateEmail, providerId);
        given(appleAuthenticator.getOAuthInfoByIdentityToken(identityToken)).willReturn(mockOAuthInfo);

        // when
        Tokens tokens = oAuthService.appleLogin(request);

        // then
        assertThat(tokens).isNotNull();
        
        User savedUser = userRepository.findByProviderId(providerId).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(privateEmail);
    }

    @Test
    @DisplayName("실제 Identity Token을 사용한 Apple 로그인 테스트")
    void appleLogin_실제IdentityToken_성공() {
        // given
        String email = "realtest@icloud.com";
        String providerId = "001234.abcd1234abcd1234abcd1234abcd1234.1234";
        String realIdentityToken = createMockAppleIdentityToken(email, providerId);
        
        AppleLoginRequest request = new AppleLoginRequest(realIdentityToken);
        OAuthInfo mockOAuthInfo = new OAuthInfo(email, providerId);
        given(appleAuthenticator.getOAuthInfoByIdentityToken(realIdentityToken)).willReturn(mockOAuthInfo);

        // when
        Tokens tokens = oAuthService.appleLogin(request);

        // then
        assertThat(tokens).isNotNull();
        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank();
        
        User savedUser = userRepository.findByProviderId(providerId).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getProvider()).isEqualTo(OAuthProvider.APPLE);
        assertThat(savedUser.getProviderId()).isEqualTo(providerId);
    }

    @Test
    @DisplayName("실제 Identity Token - 프라이빗 릴레이 이메일 테스트")
    void appleLogin_실제IdentityToken_프라이빗릴레이이메일() {
        // given
        String privateEmail = "real123test@privaterelay.appleid.com";
        String providerId = "001234.private1234private1234private1234.5678";
        String realIdentityToken = createMockAppleIdentityToken(privateEmail, providerId);
        
        AppleLoginRequest request = new AppleLoginRequest(realIdentityToken);
        OAuthInfo mockOAuthInfo = new OAuthInfo(privateEmail, providerId);
        given(appleAuthenticator.getOAuthInfoByIdentityToken(realIdentityToken)).willReturn(mockOAuthInfo);

        // when
        Tokens tokens = oAuthService.appleLogin(request);

        // then
        assertThat(tokens).isNotNull();
        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank();
        
        User savedUser = userRepository.findByProviderId(providerId).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(privateEmail);
        assertThat(savedUser.getProvider()).isEqualTo(OAuthProvider.APPLE);
        assertThat(savedUser.getProviderId()).isEqualTo(providerId);
    }

    @Test
    @DisplayName("실제 Identity Token - 이메일 업데이트 시나리오")
    void appleLogin_실제IdentityToken_이메일업데이트() {
        // given
        String privateEmail = "update123test@privaterelay.appleid.com";
        String realEmail = "updated.user@icloud.com";
        String providerId = "001234.update1234update1234update1234.9012";
        
        // 기존 사용자를 프라이빗 이메일로 생성
        User existingUser = userService.join(privateEmail, OAuthProvider.APPLE, providerId);
        existingUser.updateNickname("업데이트테스트사용자");
        userRepository.save(existingUser);
        
        // 실제 이메일이 포함된 Identity Token 생성
        String realIdentityToken = createMockAppleIdentityToken(realEmail, providerId);
        
        AppleLoginRequest request = new AppleLoginRequest(realIdentityToken);
        OAuthInfo mockOAuthInfo = new OAuthInfo(realEmail, providerId);
        given(appleAuthenticator.getOAuthInfoByIdentityToken(realIdentityToken)).willReturn(mockOAuthInfo);

        // when
        Tokens tokens = oAuthService.appleLogin(request);

        // then
        assertThat(tokens).isNotNull();
        
        User updatedUser = userRepository.findByProviderId(providerId).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo(realEmail); // 프라이빗 이메일에서 실제 이메일로 업데이트
        assertThat(updatedUser.getProvider()).isEqualTo(OAuthProvider.APPLE);
        assertThat(updatedUser.getProviderId()).isEqualTo(providerId);
        
        long userCount = userRepository.count();
        assertThat(userCount).isEqualTo(1); // 사용자는 한 명만 존재
    }

    @ParameterizedTest
    @DisplayName("다양한 실제 Identity Token 패턴 테스트")
    @ValueSource(strings = {
            "test1@icloud.com",
            "user.name@me.com", 
            "developer@mac.com",
            "testuser123@privaterelay.appleid.com",
            "a1b2c3d4e5@privaterelay.appleid.com"
    })
    void appleLogin_다양한실제이메일패턴_성공(String email) {
        // given
        String providerId = "001234.pattern" + email.hashCode() + ".test";
        String realIdentityToken = createMockAppleIdentityToken(email, providerId);
        
        AppleLoginRequest request = new AppleLoginRequest(realIdentityToken);
        OAuthInfo mockOAuthInfo = new OAuthInfo(email, providerId);
        given(appleAuthenticator.getOAuthInfoByIdentityToken(realIdentityToken)).willReturn(mockOAuthInfo);

        // when
        Tokens tokens = oAuthService.appleLogin(request);

        // then
        assertThat(tokens).isNotNull();
        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank();
        
        User savedUser = userRepository.findByProviderId(providerId).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getProvider()).isEqualTo(OAuthProvider.APPLE);
    }
}