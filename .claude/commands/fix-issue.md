---
description: Create a GitHub fix issue body using this repository's issue template.
argument-hint: "<bug or improvement summary>"
---

You are creating a fix issue for this repository.

Use Korean.

Use the repository template at `.github/ISSUE_TEMPLATE/fix-report.md`.

Input from the user:

```text
$ARGUMENTS
```

Output only the final issue content in Markdown.

Follow this exact structure:

```markdown
# Fix Report

## 고쳐야 할 사항

- ...

## 첨부자료

- ...
```

Rules:

- Keep the title suggestion separate at the top as `title: fix: ...`.
- Describe the current problem, expected behavior, and affected area when possible.
- If the issue is operational, include the relevant environment such as prod, dev, EC2, Docker, or CloudWatch.
- Do not claim a root cause unless it is directly supported by the given context.
- If there is no attachment/reference, write `- 없음`.

When the user explicitly asks to create the issue (not just draft it), run:

```bash
gh issue create --title "fix: <summary>" --body "<final markdown body>" --assignee @me --label fix
```

- Always include `--assignee @me` so the issue is assigned to the current user.
- Always include `--label fix` to match this template's convention.
