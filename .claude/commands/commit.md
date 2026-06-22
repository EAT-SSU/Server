---
description: Create a git commit message using this repository's commit convention.
argument-hint: "<brief context or leave empty to auto-detect from staged changes>"
allowed-tools: Bash(git status --short), Bash(git diff --staged)
---

You are creating a git commit message for this repository.

Use Korean.

Commit message convention:

```text
<type>: <한글로 간단하게>
```

Examples:

```text
feat: 리뷰 생성 API 추가
fix: 애플 로그인 이메일 누락 오류 수정
refactor: Partnership 기간 타입 정리
style: 코드 포맷 정리
docs: 배포 가이드 업데이트
test: 리뷰 서비스 단위 테스트 추가
chore: 의존성 버전 업데이트
```

Input from the user:

```text
$ARGUMENTS
```

Process:

1. Run `git status --short` to check staged files.
2. Run `git diff --staged` to inspect actual changes.
3. Identify the appropriate commit type from the changes.
4. Write a short Korean description summarizing what changed and why.

Allowed commit types:

```text
feat, fix, refactor, style, docs, test, chore
```

Rules:

- Description is written in Korean.
- Keep it short and clear — one line only.
- Focus on "무엇을 왜" not "어떻게".
- Do not end with a period.
- If nothing is staged, mention it and do not suggest a commit message.

Output format:

```text
커밋 메시지: feat: 리뷰 생성 API 추가
```
