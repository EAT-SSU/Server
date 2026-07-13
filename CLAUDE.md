# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Instructions

- `.claude/rules/*.md`의 `paths`가 다른 도메인 패키지에 있는 파일을 가리키는 경우(예: report.md → review 패키지의 Report 엔티티), 그 파일을 이동하거나 이름을 바꿀 때 해당 rule 파일의 `paths`도 함께 수정한다.
- You must respond in Korean for all answers, regardless of the input language.
- Do not write any comments in code blocks, even if asked to do so.
- Do not use wildcard when importing libraries.
- When you write reviews for pull request, specific review will be very helpful for my team.
- Suggest based on logical reasons.
- For every request, follow this order before writing any code:
  1. Analyze the process (현재 구조/흐름 파악)
  2. If anything is unclear or requires a decision only the user can make, ask clarifying questions before proposing a direction
  3. Suggest a direction based on the analysis (and any answers received)
  4. Present trade-offs for each direction
  5. Do NOT write code unless the user explicitly asks with a direct command such as "개발해줘", "작성해줘", "구현해줘".
- When implementing from a GitHub issue with multiple feature items, process one item at a time: complete the full analyze → clarify → direction → trade-off → explicit approval cycle for a single item before moving to the next.

## Build & Run Commands

```bash
./gradlew clean build           # 전체 빌드 (테스트 포함)
./gradlew test                  # 테스트만 실행
./gradlew test --tests "ssu.eatssu.domain.review.service.ReviewServiceTest"  # 단일 테스트 클래스 실행
./gradlew bootRun --args='--spring.profiles.active=local'  # 로컬 서버 실행 (port 9000)
docker build -f Dockerfile -t eatssu-local .  # Docker 이미지 빌드 (반드시 ./gradlew build -x test 선행 필요)
```

로컬 실행 시 MySQL이 `localhost:3306/eatssu`에 필요하며, `application-local.yml`에서 DB 정보를 설정한다. `.env` 파일로 환경변수를 오버라이드할 수 있다 (`spring.config.import: optional:file:.env[.properties]`).

QueryDSL Q클래스는 `src/main/generated`에 생성된다. `./gradlew clean`으로 제거된다.

## Architecture Overview

### 패키지 구조

```
src/main/java/ssu/eatssu/
├── domain/          # 도메인별 패키지 (auth, menu, review, user, admin, ...)
│   └── <domain>/
│       ├── entity/         # JPA 엔티티
│       ├── dto/            # Request/Response DTO
│       ├── presentation/   # Controller (일부 도메인은 controller/)
│       ├── service/        # 비즈니스 로직
│       ├── persistence/    # Repository (일부 도메인은 repository/)
│       └── ...
└── global/          # 공통 인프라
    ├── config/      # Spring 설정 (Security, QueryDSL, S3, Async 등)
    ├── handler/     # 전역 예외 처리, 응답 래퍼
    ├── log/         # AOP 로깅, MDC 필터
    ├── i18n/        # 다국어 지원 인터페이스
    └── util/        # S3 업로더 등 유틸리티
```

### 핵심 도메인

- **auth**: Kakao/Apple OAuth 로그인, JWT 토큰 발급/검증 (`JwtTokenProvider`), `CustomUserDetails`
- **menu**: `Menu`(고정 메뉴, `MenuType.FIXED`)와 `Meal`(날짜별 변동 식단, `MenuType.VARIABLE`)이 `MealMenu`를 통해 다대다 연결
- **review**: `Menu` 또는 `Meal`에 대한 리뷰. V1/V2 Controller·Service가 공존하며, V2가 최신
- **rating**: `Ratings` 임베디드 객체로 별점 집계 관리
- **user**: User 엔티티, 학과/단과대학 정보(`department` 하위 패키지), 다국어 설정(`Language` enum: KO/EN/JA/VI)
- **partnership**: 제휴 식당 정보 및 좋아요
- **admin**: 일반 API와 동일한 JWT(`ROLE_ADMIN`) 인증을 쓰는 `@ResponseBody` JSON API. 식단/메뉴/카테고리/문의/리포트/리뷰 관리. `login.mustache`가 남아있으나 어떤 컨트롤러도 뷰를 렌더링하지 않는 죽은 코드
- **slice**: 커서 기반 페이지네이션 (`SliceResponse<T>`)
- **slack**: 서버 에러 발생 시 Slack 알림 (`SlackErrorNotifier`)

### 공통 패턴

**응답 래퍼**: 모든 API 응답은 `BaseResponse<T>`로 감싼다. 에러는 `BaseException(BaseResponseStatus)` throw → `GlobalExceptionHandler`에서 처리.

**에러 코드**: `BaseResponseStatus` enum에 HTTP 상태 + 커스텀 코드 + 메시지를 함께 정의한다.

**다국어(i18n)**: `Localizable` 인터페이스의 `getLocalizedValue(language, ko, en, ja, vi)`를 구현하여 언어별 필드를 반환한다.

### API 버전 관리

일부 Controller/Service는 V1/V2로 버전이 분리되어 있다(예: `ReviewController`/`ReviewControllerV2`, `ReviewService`/`ReviewServiceV2`). 새 기능은 V2 이상에 추가하거나, 필요 시 새 버전을 생성한다.

### 배포

GitHub Actions(`cd.yml`)로 Docker 이미지를 빌드하여 EC2에 SSH 배포한다. `prod`/`dev` 브랜치에 따라 배포 환경이 분기된다.

## Commit Convention

커밋 메시지는 `feat:`, `fix:`, `refactor:`, `style:` 등의 prefix를 사용한다.

## PR Convention

`.github/PULL_REQUEST_TEMPLATE.md`를 따른다: `resolved #이슈번호`, 변경 사항 요약, 리뷰어 공유사항 작성.
