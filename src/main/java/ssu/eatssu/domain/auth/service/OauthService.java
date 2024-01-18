package ssu.eatssu.domain.auth.service;


import static ssu.eatssu.global.handler.response.BaseResponseStatus.INVALID_IDENTITY_TOKEN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.auth.entity.OauthProvider;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.domain.user.service.UserService;
import ssu.eatssu.domain.auth.dto.OauthInfo;
import ssu.eatssu.domain.auth.dto.AppleKeys;
import ssu.eatssu.domain.user.dto.Tokens;

import java.math.BigInteger;
import java.net.URI;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import ssu.eatssu.global.handler.response.BaseException;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OauthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final RestTemplate restTemplate;

    /**
     * 카카오 로그인
     */
    public Tokens kakaoLogin(String email, String providerId) {

        //가입 안된 유저일 경우 회원가입 진행
        User user = userRepository.findByProviderId(providerId)
                .orElseGet(() -> join(email, OauthProvider.KAKAO, providerId));

        // return Tokens
        return userService.generateOauthJwtTokens(user.getEmail(), OauthProvider.KAKAO, providerId);
    }

    /**
     * 애플 로그인
     */

    public Tokens appleLogin(String identityToken) {

        //애플 유저 정보 조회
        OauthInfo oauthInfo = getUserInfoFromApple(identityToken);


        //가입 안된 유저일 경우 회원가입 진행
        User user = userRepository.findByProviderId(oauthInfo.providerId())
                .orElseGet(() -> join(oauthInfo.email(), OauthProvider.APPLE, oauthInfo.providerId()));


        //이메일 갱신
        updateAppleUserEmail(user, oauthInfo.email());

        // return Tokens
        return userService.generateOauthJwtTokens(user.getEmail(), OauthProvider.APPLE, oauthInfo.providerId());
    }

    /**
     * 회원가입
     */

    private User join(String email, OauthProvider provider, String providerId) {
        String credentials = createCredentials(provider, providerId);

        //회원가입
        User user = User.oAuthJoin(email, provider, providerId, credentials);
        return userRepository.save(user);
    }

    /**
     * Credentials 생성
     */

    private String createCredentials(OauthProvider provider, String providerId) {
        return passwordEncoder.encode(provider + "_" + providerId);
    }

    /**
     * 애플 유저 private email -> 찐 email 로 갱신
     */
    private void updateAppleUserEmail(User user, String email) {

        if (isHideEmail(user.getEmail()) && !isHideEmail(email)) {
            user.updateEmail(email);
            userRepository.save(user);
        }
    }

    /**
     * 애플 로그인 - 유저 정보(providerId, email) 조회
     */
    private OauthInfo getUserInfoFromApple(String identityToken) {

        PublicKey publicKey = generatePublicKey(identityToken);

        return getUserInfoByPublicKey(identityToken, publicKey);
    }

    /**
     * 애플 로그인 - PublicKey 를 통해 유저 정보(providerId, email) 조회
     */
    private OauthInfo getUserInfoByPublicKey(String identityToken, PublicKey publicKey) {

        // identityToken 에서 publicKey 서명을 통해 Claims 를 추출한다.
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .build()
            .parseClaimsJws(identityToken)
            .getBody();

        //Claims 에서 email, providerId(사용자 식별값) 를 추출한다.
        try {
            String email = claims.get("email").toString();
            String providerId = claims.get("sub").toString();
            return new OauthInfo(email, providerId);
        } catch (ExpiredJwtException exception) {
            throw new BaseException(INVALID_IDENTITY_TOKEN);
        }
    }

    /**
     * 애플 로그인 - PublicKey 를 생성한다.
     */
    private PublicKey generatePublicKey(String identityToken) {

        //PublicKey 를 만들기 위한 재료가 되는 후보 Key 목록을 가져온다.
        AppleKeys keys = getAppleKeys();

        //후보 Key 에서 정답 Key 를 찾는다.
        AppleKeys.Key matchedKey = selectMatchedKey(identityToken, keys);

        //정답 Key 를 통해 PublicKey 를 생성한다.
        return generatePublicKeyWithApplePublicKey(matchedKey);

    }

    /**
     * 애플 로그인 - 정답 Key 를 통해 PublicKey 를 생성한다.
     */
    private PublicKey generatePublicKeyWithApplePublicKey(AppleKeys.Key matchedKey) {

        //정답 키에서 PublicKey 의 재료가 될 n, e 값을 가져온다.
        byte[] nBytes = Base64.getUrlDecoder().decode(matchedKey.getN());
        byte[] eBytes = Base64.getUrlDecoder().decode(matchedKey.getE());

        BigInteger n = new BigInteger(1, nBytes);
        BigInteger e = new BigInteger(1, eBytes);

        // n, e 값을 통해 PublicKeySpec 을 세팅한다.
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);

        //PublicKeySpec 을 통해 PublicKey 를 생성한다.
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(matchedKey.getKty());
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new BaseException(INVALID_IDENTITY_TOKEN);
        }
    }

    /**
     * 애플 로그인 - 헤더에서 뽑은 정보를 통해 후보 Key 에서 정답 Key 를 찾아서 반환한다.
     */
    private AppleKeys.Key selectMatchedKey(String identityToken, AppleKeys candidateKeys) {

        //identity token 에서 header 를 뽑아서 decode 한다.
        String header = identityToken.split("\\.")[0];
        String decodedHeader = new String(Base64.getDecoder().decode(header));

        //decode 된 header 정보를 통해 정답키의 key id, algorithm 정보를 가져온다.
        Map<String, String> headerMap;
        try {
            headerMap = new ObjectMapper().readValue(decodedHeader, new TypeReference<Map<String, String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new BaseException(INVALID_IDENTITY_TOKEN);
        }

        //후보키 중에서 정답키를 찾아서 반환한다.
        return candidateKeys.findKeyBy(headerMap.get("kid"), headerMap.get("alg"))
            .orElseThrow(() -> new BaseException(INVALID_IDENTITY_TOKEN));
    }

    /**
     * 애플 로그인 - Apple api 호출을 통해  apple 후보 key 리스트를 받아온다.
     */
    private AppleKeys getAppleKeys() {

        URI uri = UriComponentsBuilder
            .fromUriString("https://appleid.apple.com")
            .path("/auth/keys")
            .encode()
            .build()
            .toUri();

        ResponseEntity<AppleKeys> response = restTemplate.getForEntity(uri, AppleKeys.class);
        return response.getBody();
    }

    /**
     * 애플 로그인 - 이메일 가리기 여부 확인
     */
    private boolean isHideEmail(String email) {
        if (email.length() > 25) {
            return email.startsWith("@privaterelay.appleid.com", email.length() - 25);
        } else {
            return false;
        }
    }

}
