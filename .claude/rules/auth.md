---
paths:
  - "src/main/java/ssu/eatssu/domain/auth/**/*.java"
---

# Auth Domain Notes

- Access/refresh 토큰이 클레임 구조상 구분되지 않는다(`token_type` 클레임 없음). `JwtTokenProvider`의 `validateToken`/`getAuthentication`은 refresh 토큰도 access 토큰 자리에서 그대로 통과시킨다.
- Public 엔드포인트 화이트리스트는 `SecurityConfig.AUTH_WHITELIST`/`RESOURCE_LIST`와 `JwtAuthenticationFilter.AUTH_WHITELIST` 두 곳에 중복 관리된다. 새 public 엔드포인트를 추가할 때는 반드시 두 곳을 함께 수정한다.
- OAuth 로그인의 "비밀번호"는 `provider + providerId`를 BCrypt 해시한 값이다(`UserService.createCredentials`/`OAuthService.makeOauthCredentials`). 이 조합 로직을 변경하면 기존 유저 전원이 로그인할 수 없게 된다.
- 동일 이메일로 다른 provider(Kakao/Apple)로 로그인하면 `findFirstByEmailOrderByIdAsc`로 기존 계정에 조용히 합쳐진다(코드 내 FIXME로 명시된 known issue). `users` 테이블에 email unique 제약은 없다.
- Apple 이메일 릴레이 감지(`OAuthService.isHideEmail`)는 `@privaterelay.appleid.com` 접미사 + 길이 25자 초과라는 하드코딩된 조건에 의존한다.
- V1(`/oauths/kakao`, `/oauths/apple`)과 V2(`/oauths/v2/kakao`, `/oauths/v2/apple`)가 공존하며 V1은 로그인/회원가입 마이그레이션 이후 삭제 예정으로 표시되어 있다. 새 기능은 V2 기준으로 작성한다.
- CORS는 `addAllowedOriginPattern("*")` + `setAllowCredentials(true)`로 모든 출처를 허용한다.
