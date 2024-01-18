package ssu.eatssu.domain.auth.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.user.dto.Tokens;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private Key key;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret.key}") String secret,
                            @Value("${jwt.token-validity-in-seconds}") long accessTokenValidityInSeconds,
                            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInMilliseconds
                            //RedisTemplate redisTemplate*
                            ) {
        this.secret = secret;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds * 1000;
    }

    @PostConstruct
    public void setKey(){ //jwt.secret을 이요하여 key생성
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //토큰 발급
    public Tokens generateTokens(Authentication authentication){

        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        UserPrincipalDto userPrincipalDto = UserPrincipalDto.from((CustomUserDetails) authentication.getPrincipal());

        Claims claims = createClaims(userPrincipalDto, authorities);

        return new Tokens(createAccessToken(claims), createRefreshToken(claims));
    }

    private Claims createClaims(UserPrincipalDto userPrincipalDto, String authorities) {

        String subject;
        try{
            subject = objectMapper.writeValueAsString(userPrincipalDto);
        } catch (JsonProcessingException e) {
            log.error("Cannot generate JWT Tokens because an error occurred during json parsing.");
            log.error("errorTrackStace: {}", (Object) e.getStackTrace());
            throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }

        Claims claims = Jwts.claims()
                .setSubject(subject);
        claims.put(AUTHORITIES_KEY, authorities);

        return claims;
    }

    //accessToken 생성
    private String createAccessToken(Claims claims) {
        long now  = new Date().getTime(); //밀리세컨드
        Date accessTokenValidity = new Date(now + this.accessTokenValidityInMilliseconds);
        claims.setExpiration(accessTokenValidity);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    //refreshToken 생성
    private String createRefreshToken(Claims claims) {
        long now  = new Date().getTime(); //밀리세컨드
        Date refreshTokenValidity = new Date(now + this.refreshTokenValidityInMilliseconds);
        claims.setExpiration(refreshTokenValidity);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    //token 에서 Authentication 추출
    public Authentication getAuthentication(String token) throws JsonProcessingException {
        Claims claims = getClaims(token);

        List<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();


        UserPrincipalDto userPrincipalDto = objectMapper.readValue(claims.getSubject(), UserPrincipalDto.class);

        CustomUserDetails principal = new CustomUserDetails(userPrincipalDto.getId(),userPrincipalDto.getEmail(),  "",
                authorities.get(0));

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //토큰 유효성 검증
    public boolean validateToken(String token) throws BaseException{
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // 토큰 만료 시간 확인(밀리세컨드)
    public Long getExpiration(String accessToken) {
        Date expiration = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration();

        Long now = new Date().getTime();

        return (expiration.getTime() - now);
    }







}
