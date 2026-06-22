# Repository Guidelines

## Project Structure & Module Organization

This is a Spring Boot 3 / Java 17 backend built with Gradle. Application code lives under `src/main/java/ssu/eatssu`, grouped by domain such as `auth`, `menu`, `review`, `user`, `admin`, and shared infrastructure under `global`. Runtime configuration is in `src/main/resources`, with profile files such as `application-dev.yml` and `application-prod.yml`. Flyway migrations are in `src/main/resources/db/migration`. Static admin assets and Mustache templates are under `src/main/resources/static` and `src/main/resources/templates`. Tests live in `src/test/java`, with test configuration in `src/test/resources`.

## Build, Test, and Development Commands

- `./gradlew clean build`: compile, test, and build the application jar.
- `./gradlew test`: run the JUnit test suite.
- `./gradlew bootRun --args='--spring.profiles.active=local'`: run the server locally on port `9000`.
- `docker build -f Dockerfile -t eatssu-local .`: build the Docker image locally.

The CI workflow currently builds Docker images for prod/dev and deploys to EC2 via SSH.

## Coding Style & Naming Conventions

Use Java 17 conventions with 4-space indentation. Do not use wildcard imports. Keep domain logic in service classes, persistence in repository/query classes, and request/response shapes in DTOs. Follow existing naming patterns: `*Controller`, `*Service`, `*Repository`, `*Request`, and `*Response`. Comments should be short and useful; follow the project convention for `FIX ME` comments when marking unresolved business or technical concerns.

## Testing Guidelines

Tests use JUnit 5 and Spring Boot Test. Test method names should be written in English. For test code, keep `given`, `when`, and `then` comments where they clarify setup and assertions. Run `./gradlew test` before opening a PR. If tests cannot be run because of local configuration, state that clearly in the PR reviewer notes.

## Commit & Pull Request Guidelines

Recent commits use prefixes such as `feat:`, `fix:`, `refactor:`, and `style:`; keep commit messages concise and behavior-focused. Pull requests should follow `.github/PULL_REQUEST_TEMPLATE.md`: link the issue with `resolved #`, summarize what changed and why, and include reviewer notes for risks, test gaps, or deployment concerns. Use the issue templates in `.github/ISSUE_TEMPLATE` for feature and fix reports.

## Security & Configuration Tips

Never commit secrets, tokens, or local credentials. Keep local-only configuration in ignored files such as `.env` or `application-local.yml`. Be careful with Docker and deployment changes: production logs go to CloudWatch via the `awslogs` driver, and prod/dev behavior is selected by branch-specific GitHub Actions steps.

## Agent-Specific Workflow

For every request, analyze before implementing. Start by explaining the relevant process, propose a direction, and state key trade-offs. Do not write or modify code unless the user explicitly asks with direct wording such as "개발해줘", "작성해줘", "수정해줘", or an equivalent implementation request. If the request is exploratory, advisory, or ambiguous, provide analysis and recommendations only.
