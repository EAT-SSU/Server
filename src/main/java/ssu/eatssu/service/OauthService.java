package ssu.eatssu.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.enums.OauthProvider;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.jwt.JwtTokenProvider;
import ssu.eatssu.response.BaseException;
import ssu.eatssu.web.oauth.dto.AppleKeys;
import ssu.eatssu.web.user.dto.Tokens;

import java.math.BigInteger;
import java.net.URI;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import static ssu.eatssu.response.BaseResponseStatus.FAIL_MAKE_TOKEN;
import static ssu.eatssu.response.BaseResponseStatus.INVALID_TOKEN;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OauthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    private void join(String email, String providerId, OauthProvider provider) {
        String pwd = makePassword(provider, providerId);
        String encodedPwd = passwordEncoder.encode(pwd);
        User user = User.oAuthJoin(email, encodedPwd, provider + "유저", provider, providerId);
        userRepository.save(user);
    }

    public Tokens generateJwtTokens(String email, String pwd){
        // 1. email/pwd 를 기반으로 Authentication 객체 생성
        //    이때 authentication은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, pwd);

        // 2. 실제 검증 (사용자 비밀번호 체크)
        //    authenticate 메서드 실행 => CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 바탕으로 JWT 토큰 생성
        try{
            return jwtTokenProvider.generateTokens(authentication);
        } catch (JsonProcessingException e){
            throw new BaseException(FAIL_MAKE_TOKEN);
        }
    }

    private String makePassword(OauthProvider provider, String providerId) {
        return provider.toString() + providerId;
    }

    public Tokens loginByKakao(String email, String providerId) {
        Optional<User> user = userRepository.findByProviderId(providerId);
        if (user.isEmpty()) {
            join(email, providerId, OauthProvider.KAKAO);
        }
        String pwd = makePassword(OauthProvider.KAKAO, providerId);
        return generateJwtTokens(email, pwd);
    }

    public Tokens loginByApple(String identityToken) throws NoSuchAlgorithmException, InvalidKeySpecException {
        OauthInfo oauthInfo = userInfoFromApple(identityToken);
        Optional<User> user = userRepository.findByProviderId(oauthInfo.getProviderId());
        if (user.isEmpty()) {
            join(oauthInfo.getEmail(), oauthInfo.getProviderId(), OauthProvider.APPLE);
        }
        String pwd = makePassword(OauthProvider.APPLE, oauthInfo.getProviderId());
        return generateJwtTokens(oauthInfo.getEmail(), pwd);
    }

    private OauthInfo userInfoFromApple(String identityToken) throws NoSuchAlgorithmException, InvalidKeySpecException {

        //identity token decode
        String[] decodeArray = identityToken.split("\\.");
        String headerOfToken = new String(Base64.getDecoder().decode(decodeArray[0]));
        Map<String, String> headerMap;
        try{
            headerMap = new ObjectMapper().readValue(
                    headerOfToken, new TypeReference<Map<String,String>>(){});
        } catch (JsonProcessingException e) {
            throw new BaseException(INVALID_TOKEN);
        }
        URI uri = UriComponentsBuilder
                .fromUriString("https://appleid.apple.com")
                .path("/auth/keys")
                .encode()
                .build()
                .toUri();

        ResponseEntity<AppleKeys> response = restTemplate.getForEntity(uri, AppleKeys.class);
        AppleKeys keys = response.getBody();
        AppleKeys.Key matchedKey = keys.findKeyBy(headerMap.get("kid"), headerMap.get("alg"))
                .orElseThrow(() -> new BaseException(INVALID_TOKEN));

        byte[] nBytes = Base64.getUrlDecoder().decode(matchedKey.getN());
        byte[] eBytes = Base64.getUrlDecoder().decode(matchedKey.getE());


        BigInteger n = new BigInteger(1, nBytes);
        BigInteger e = new BigInteger(1, eBytes);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
        KeyFactory keyFactory = KeyFactory.getInstance(matchedKey.getKty());
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        try{
            Claims claims = Jwts.parserBuilder().setSigningKey(publicKey).build()
                    .parseClaimsJws(identityToken).getBody();
            String email = claims.get("email").toString();
            String providerId = claims.get("sub").toString();
            return new OauthInfo(email,providerId);

        }catch (ExpiredJwtException exception){
            log.info(exception.getMessage());
            throw new BaseException(INVALID_TOKEN);
        }
    }

    @AllArgsConstructor
    @Getter
    private static class OauthInfo{
        private String email;
        private String providerId;
    }
}
