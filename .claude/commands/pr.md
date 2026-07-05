---
description: Draft a pull request body using this repository's PR template and current git changes.
argument-hint: "<issue number or brief context>"
allowed-tools: Bash(git status --short), Bash(git diff --stat), Bash(git diff --name-only), Bash(git diff)
---

You are drafting a pull request for this repository.

Use Korean.

Use the repository template at `.github/PULL_REQUEST_TEMPLATE.md`.

Input from the user:

```text
$ARGUMENTS
```

Before writing the PR body:

1. Check changed files with `git status --short`.
2. Check the diff summary with `git diff --stat`.
3. Inspect relevant diffs with `git diff`.

Output only the final PR content in Markdown.

Follow this exact structure:

```markdown
## #️⃣ Issue Number

- resolved #

## 📝 요약(Summary)

-

## 💬 공유사항 to 리뷰어

-

## ✅ PR Checklist

PR이 다음 요구 사항을 충족하는지 확인하세요.

- [ ] 커밋 메시지 컨벤션에 맞게 작성했습니다.
- [ ] 변경 사항에 대한 테스트를 했습니다.(버그 수정/기능에 대한 테스트).
```

Rules:

- Fill `resolved #` with an issue number if the user provided one. If not, keep `resolved #`.
- Explain what changed and why, not just how.
- Mention key files or behavior changes when helpful.
- In `공유사항 to 리뷰어`, call out risks, test gaps, migration concerns, deployment concerns, or areas needing focused review.
- For the checklist, mark an item checked only if the evidence is clear from the context or command results.
- If tests were not run, leave the test checkbox unchecked and mention that in reviewer notes.
- Do not include unrelated refactoring in the summary.

When the user explicitly asks to create the PR (not just draft it), run:

```bash
gh pr create --title "<type>: <summary>" --body "<final markdown body>" --assignee @me --reviewer eunseo9311,sjinssun --label <label>
```

- Always include `--assignee @me` so the PR is assigned to the current user.
- Always include `--reviewer eunseo9311,sjinssun`.
- Determine `<label>` from the current branch's type prefix (`feat/`, `fix/`, `refactor/`, `docs/`, `test/`, `chore/`, `style/`) using this mapping:

  | Branch type | Label |
  | --- | --- |
  | feat | feat |
  | fix | fix |
  | refactor | refactoring |
  | docs | docs |
  | test | test |
  | chore | chore |
  | style | (no label — omit `--label`) |

  If the branch type has no matching label in the table, omit `--label` entirely rather than guessing.
