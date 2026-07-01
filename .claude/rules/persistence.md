---
paths:
  - "**/persistence/**/*.java"
  - "**/repository/**/*.java"
  - "**/entity/**/*.java"
  - "src/main/resources/db/migration/**"
---

# Persistence & Migration Convention

- Complex queries use the `*RepositoryCustom` interface + `*RepositoryImpl` (QueryDSL) pattern.
- Schema is managed with Flyway migration files at `src/main/resources/db/migration/V{n}__{설명}.sql`.
- `ddl-auto` is set to `none`, so any schema change must be accompanied by a new migration file.
