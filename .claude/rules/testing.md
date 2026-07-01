---
paths:
  - "src/test/**/*.java"
---

# Test Code Convention

- Test method names must be written in **English**.
- If the code is related to test, write comments for "given, when, then".
- Tests run with `@SpringBootTest` connected to a real DB. Do not mock the DB.
