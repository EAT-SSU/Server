---
paths:
  - "src/main/java/ssu/eatssu/domain/user/**/*.java"
---

# User Domain Notes

- `users` 테이블에 email unique 제약이 없다. 다른 provider로 같은 이메일 로그인 시 `findFirstByEmailOrderByIdAsc`로 기존 계정에 합쳐진다([[auth]]와 연결된 이슈).
- `College`/`Department`의 `name_en`은 어떤 마이그레이션에서도 채워진 적이 없다(V10/V11은 ja/vi만 채움). EN 유저는 college/department 이름이 항상 한국어로 폴백된다.
- 회원 탈퇴(`UserService.withdraw`)는 하드 delete이지만 엔티티마다 처리 방식이 다르다: `Review`/`Inquiry`는 `clearUser()`로 FK만 null 처리해 데이터를 보존하고, `reviewLikes`/`partnershipLikes`(`orphanRemoval=true`)와 `reviewReports`(`cascade=ALL`)는 그대로 cascade 삭제된다. `User`에 새 `@OneToMany` 연관관계를 추가할 때 탈퇴 시 어떻게 처리할지 반드시 정해야 한다.
- `NicknameValidator`는 한글/영문 비속어·브랜드명·관리자 사칭 방지 정규식을 담은 코드로, 수정 빈도는 낮지만 잘못 건드리면 닉네임 검증 전체(등록/검증 두 진입점 모두)에 영향을 준다.
