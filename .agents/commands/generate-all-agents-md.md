# Prompt: Generate AGENTS.md for all modules in a Maven multi-module project

## Task

You are working at the root of a Maven multi-module project located at `{{ROOT_PATH}}`.

Your goal is to:
1. Discover all Maven modules in the project.
2. Analyze the full project to extract shared, global conventions.
3. Write a root `{{ROOT_PATH}}/AGENTS.md` covering project-wide context.
4. Write a `AGENTS.md` for each submodule, inheriting from the root and only
   adding what is specific to that module.

Do not ask for clarification. Work through all steps sequentially and completely.

---

## Step 1 — Discover all modules

Read `{{ROOT_PATH}}/pom.xml`.

Extract every `<module>` entry. For each one, check whether its own `pom.xml`
contains further `<module>` entries (i.e. nested aggregators). Repeat recursively
until you have a flat list of all leaf modules — the ones that contain actual
source code under `src/`.

Build a module inventory in this format before proceeding:

```
MODULE INVENTORY
─────────────────────────────────────────────
Root aggregator : {{ROOT_PATH}}
Modules found   : N

  [1] module-alpha         → {{ROOT_PATH}}/module-alpha
  [2] module-beta          → {{ROOT_PATH}}/module-beta
  [3] parent-group/child-a → {{ROOT_PATH}}/parent-group/child-a
  ...
─────────────────────────────────────────────
```

Print this inventory before writing any files. This makes the plan visible and
lets the user catch missing or wrongly discovered modules early.

---

## Step 2 — Analyze the full project (read before writing)

Before writing a single file, read the following across all modules:

**Global reads (do once):**
- `{{ROOT_PATH}}/pom.xml` — Java version, encoding, compiler plugin, BOM imports,
  shared dependency management, SCM, distributionManagement.
- `{{ROOT_PATH}}/.mvn/` — Maven wrapper config, JVM config, extensions.
- Any root-level config files: `.editorconfig`, `checkstyle.xml`, `spotbugs.xml`,
  `pmd.xml`, `jacoco` config, `lombok.config`, `sonar-project.properties`.
- Root `README.md` if present — extract project description and high-level architecture.
- Root `docs/` or `adr/` folder if present — note Architecture Decision Records.

**Per-module reads (repeat for each module):**
- `<module>/pom.xml`
- `<module>/src/main/java/` — package structure, key classes, annotations.
- `<module>/src/main/resources/` — `application*.yml`, `application*.properties`,
  `bootstrap*.yml`, `logback*.xml`, any `META-INF/` entries.
- `<module>/src/test/java/` — test base classes, annotations, patterns.
- `<module>/src/test/resources/` — test config, fixtures, SQL scripts.

After reading, build an internal mental model:
- Which modules are libraries vs. runnable services?
- Which modules share the same framework (Spring Boot, Quarkus, plain Java)?
- Which patterns repeat across modules (same base classes, same exception hierarchy,
  same annotation usage)?
- Which modules depend on which other modules?

---

## Step 3 — Write `{{ROOT_PATH}}/AGENTS.md`

This file is the single source of truth for project-wide context. Every
module-level `AGENTS.md` will reference it. Do NOT repeat its contents in
submodule files — only override or extend.

Use the following structure:

---

```markdown
# AGENTS.md — <Project Name>

> This file is the root context document for AI coding agents.
> Module-specific context lives in each module's own `AGENTS.md`.

## Project overview
- What this project is and what it does (2–4 sentences).
- Business domain and high-level purpose.
- Type of system: monorepo of microservices / modular monolith / shared libraries / etc.

## Repository layout
Describe the top-level structure:
- Which modules are runnable services vs. shared libraries vs. BOM/parent POMs.
- Any special directories (docs, scripts, infra, etc.).

## Technology stack
- Java version (from compiler plugin or `<java.version>` property).
- Build tool and version (Maven wrapper version from `.mvn/wrapper/`).
- Primary framework(s) and versions.
- Key shared libraries (e.g. MapStruct, Lombok, jOOQ, Flyway, Testcontainers).
- Observability stack if present (Micrometer, OpenTelemetry, etc.).

## Module dependency graph
Show inter-module dependencies as a simple list:
  module-alpha  →  (no internal deps)
  module-beta   →  module-alpha
  module-gamma  →  module-alpha, module-beta

## Global build & test commands
```bash
# Build everything
mvn clean install -DskipTests

# Build everything including tests
mvn clean install

# Build a single module and its dependencies
mvn -pl <module> -am clean install

# Run all tests across all modules
mvn test

# Run tests for a specific module
mvn -pl <module> test

# Run with a specific profile
mvn clean install -P <profile-name>
```
List all known Maven profiles and what they activate.

## Code style & static analysis
- Checkstyle / PMD / SpotBugs: where the config lives, how to run, how to suppress.
- Formatter: which formatter (google-java-format, Eclipse, IntelliJ), config file location.
- Lombok: note that `@Data`, `@Builder`, etc. are used — do not manually write
  equals/hashCode/toString on classes that already have Lombok annotations.
- Any annotation processors in use and what they generate.

## Global coding conventions
Rules that apply to every module:
- Package naming scheme (e.g. `com.acme.<domain>.<layer>`).
- Exception hierarchy root class and how errors should propagate.
- Logging framework and conventions (SLF4J + Logback; use `log.info(...)` not
  `System.out`; MDC fields expected, etc.).
- Transaction boundary rules (e.g. `@Transactional` only on service layer).
- Null safety policy (e.g. `@NonNull` / `@Nullable` from which package, Optional usage).
- Thread-safety expectations.

## Testing conventions
- Test naming convention (e.g. `MethodName_StateUnderTest_ExpectedBehavior`).
- Base test classes all tests should extend (if any).
- Mocking library (Mockito, EasyMock) and preferred style (annotations vs. programmatic).
- Integration test separation strategy (separate source set, Maven profile, naming suffix).
- Testcontainers: is there a shared container setup? Where?
- Code coverage threshold (Jacoco minimum, if configured).

## CI/CD context
- Brief description of the pipeline (GitHub Actions, Jenkins, GitLab CI, etc.).
- Branch strategy (trunk-based, Gitflow, etc.).
- Where to find pipeline config files.
- Any quality gates an agent's changes must pass.

## Files and areas agents must never modify
- Auto-generated files (list patterns, e.g. `**/generated-sources/**`, `*MapperImpl.java`).
- Infrastructure/IaC files if present.
- Any file explicitly marked with a "do not edit" header comment.

## External systems and contracts
- Databases: which modules own which schemas.
- Message brokers: topic/queue naming conventions.
- External APIs: which modules are consumers/producers, where specs live.

## Glossary (optional)
Define domain terms that appear in class names, method names, and configs so
agents understand their meaning without reading business documentation.
```

---

## Step 4 — Write `<module>/AGENTS.md` for each submodule

Repeat this step for every module in the inventory from Step 1.

Each file must:
- Open with a one-line back-reference: `> Inherits global context from [root AGENTS.md](../AGENTS.md).`
  (adjust the relative path depth as needed).
- Only contain information that is **specific to this module** or **differs from
  the root**. Do not copy global conventions.
- Be concise. A focused 60-line file is more useful than a padded 200-line one.

Use this structure:

---

```markdown
# AGENTS.md — <module-name>

> Inherits global context from [root AGENTS.md](../../AGENTS.md).

## Module purpose
One paragraph: what this module does, what problem it solves, and how it fits
into the broader system. Is it a service, a library, a batch job, a gateway?

## Architecture overview
- Internal layering specific to this module.
- Design patterns in use here (if different from or additional to global conventions).
- Framework specifics (e.g. this module uses Spring Batch while others use Spring MVC).

## Entry points
Key classes an agent must know to orient itself:

| Class | Role |
|---|---|
| `com.acme.foo.FooController` | REST entry point, handles `/api/foo/**` |
| `com.acme.foo.FooService` | Core business logic |
| `com.acme.foo.FooRepository` | Data access, owns the `foo` table |
| `com.acme.foo.FooApplication` | Spring Boot main class (if runnable) |

## Module dependencies
Internal (other project modules):
- `module-alpha` — used for shared domain types

External (notable libs not in root, or used in a specific way here):
- `org.springframework.batch:spring-batch-core` — batch job orchestration

## Build & test commands
```bash
# From repo root
mvn -pl <relative-module-path> -am clean install

# Tests only
mvn -pl <relative-module-path> test

# Integration tests (if separate profile exists)
mvn -pl <relative-module-path> test -P integration-tests
```
Required environment variables or running services for tests:
- `DB_URL` — PostgreSQL connection string (or Testcontainers auto-starts one)

## Configuration
| Property / Env var | Source | Description |
|---|---|---|
| `spring.datasource.url` | `application.yml` | JDBC URL |
| `APP_SECRET_KEY` | Environment / Vault | Signing key for tokens |
| `feature.new-flow.enabled` | `application.yml` | Feature flag |

Active profiles: `dev`, `staging`, `prod` — differences described here if relevant.

## Module-specific coding conventions
Only list rules that override or extend the root conventions:
- MapStruct mappers live in `com.acme.foo.mapper` — do not add conversion logic
  to domain classes directly.
- All public service methods must be covered by a unit test in `FooServiceTest`.

## Constraints and fragile areas
- `src/main/java/com/acme/foo/generated/` — auto-generated by jOOQ, never edit.
- `FooLegacyAdapter.java` — technical debt, requires human review before changes.
- The `foo_events` Kafka topic schema is owned by team X; coordinate before changing
  any event class under `com.acme.foo.event`.

## Testing strategy
- Unit tests: plain JUnit 5 + Mockito, no Spring context.
- Integration tests: `@SpringBootTest` + Testcontainers (PostgreSQL + Kafka).
- Test data: SQL fixtures in `src/test/resources/db/`.
- Known flaky test: `FooIntegrationTest#shouldHandleTimeout` — skip in CI with
  `-Dexclude=**/FooIntegrationTest.java` until resolved.

## Related context
- [OpenAPI spec](src/main/resources/openapi/foo-api.yml)
- [ADR-0012: Why we chose batch over streaming](../../docs/adr/0012-batch-vs-streaming.md)
- [Runbook](https://confluence.acme.com/display/FOO/Runbook)
```

---

## Step 5 — Final consistency pass

After writing all files, perform a consistency check:

1. **Cross-reference check**: every module listed in the root `AGENTS.md` dependency
   graph has a corresponding `AGENTS.md` file.
2. **Duplication check**: no module file repeats a section already covered identically
   in the root file.
3. **Path check**: all relative links between files use the correct `../` depth.
4. **Command check**: every `mvn` command uses the actual module directory name as
   it appears in the filesystem, not the Maven `<artifactId>`.
5. **Generated-file check**: any class ending in `MapperImpl`, `$Impl`, or living
   under `generated-sources` is flagged as auto-generated in the relevant module's
   constraints section.

Report the result of this check as a summary after all files are written:

```
GENERATION SUMMARY
─────────────────────────────────────────────
Root AGENTS.md     : written → {{ROOT_PATH}}/AGENTS.md
Module AGENTS.md   : written N files
  ✓  module-alpha  → module-alpha/AGENTS.md
  ✓  module-beta   → module-beta/AGENTS.md
  ✓  parent/child  → parent/child/AGENTS.md
  ...
Consistency checks : all passed / N warnings (listed below)
─────────────────────────────────────────────
```

List any warnings (missing info, ambiguous patterns, modules with no source code
that were skipped, etc.) so the developer can manually fill in the gaps.

---

## Quality principles

- **Read first, write second.** Never invent class names, property keys, or
  commands. Everything in the output must be grounded in files you actually read.
- **Concise over complete.** A module file that fits on one screen is more useful
  to an agent than an exhaustive one that buries the important parts.
- **Inherit, don't repeat.** The root file exists so module files stay short.
  If a convention is already in the root, do not copy it into the module file.
- **Constraints are the most valuable section.** Telling an agent what NOT to
  touch prevents more damage than telling it what to do.
- **Leave placeholders honestly.** If information cannot be inferred from the
  source files (e.g. external service URLs, secret names, Confluence links),
  write `<!-- TODO: fill in -->` rather than inventing a value.
