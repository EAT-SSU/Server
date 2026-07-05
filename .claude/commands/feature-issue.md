---
description: Create a GitHub feature issue body using this repository's issue template.
argument-hint: "<feature summary>"
---

You are creating a feature issue for this repository.

Use Korean.

Use the repository template at `.github/ISSUE_TEMPLATE/feature-report.md`.

Input from the user:

```text
$ARGUMENTS
```

Output only the final issue content in Markdown.

Follow this exact structure:

```markdown
# Feature Report

## 추가 할 사항들

- ...

## 첨부자료

- ...
```

Rules:

- Keep the title suggestion separate at the top as `title: feature: ...`.
- If the user did not provide enough detail, infer a reasonable feature scope from the repository context.
- Do not invent implementation details that are not implied by the request.
- Prefer concrete acceptance criteria over vague descriptions.
- If there is no attachment/reference, write `- 없음`.

When the user explicitly asks to create the issue (not just draft it), run:

```bash
gh issue create --title "feature: <summary>" --body "<final markdown body>" --assignee @me --label feat
```

- Always include `--assignee @me` so the issue is assigned to the current user.
- Always include `--label feat` to match this template's convention.
