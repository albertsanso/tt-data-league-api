# AGENTS.md — tt-data-league-api

> This file is the root context document for AI coding agents.
> Module-specific context lives in each module's own `AGENTS.md`.

## Project overview

A Spring Boot-based API platform for managing table tennis league data. This project provides REST and GraphQL interfaces to a core domain model, implementing a layered architecture with Command Query Responsibility Segregation (CQRS) patterns using the Albert Sanso event bus libraries. The system manages clubs, practitioners, players, matches, and season data for competitive table tennis leagues.

**Type of system:** Modular monolith with runnable service + multiple API layer libraries.

## Repository layout

```
tt-data-league-api/                          (Root aggregator, this repo)
├── .github/                                 (GitHub workflows; OpenAPI validation pipeline)
├── .agents/                                 (Prompt commands used to generate/update AGENTS files)
├── tt-data-league-api-runtime               (Spring Boot main service)
├── tt-data-league-api-rest                  (REST API controllers & DTOs)
├── tt-data-league-api-graphql               (GraphQL resolvers & schema)
├── tt-data-league-api-core                  (Business logic: command/query handlers)
├── tt-data-league-api-repository-jpa        (JPA repository implementations)
├── docs/                                    (Documentation on build, API generation)
├── scripts/                                 (Helper scripts for OpenAPI processing)
├── docker/                                  (Docker Compose files for local dev)
└── prompts/                                 (AI agent guidance templates)
```

**External dependencies** (managed in a separate repository):
- `tt-data-league-core-domain` — domain entities and contracts (org.cttelsamicsterrassa group)
- `tt-data-league-core-repository-jpa` — base JPA repository interfaces

## Technology stack

- **Java version:** 21 (compiler source/target)
- **Build tool:** Maven (wrapper in `.mvn/`)
- **Spring Boot:** 3.5.8
- **Primary frameworks:**
  - Spring MVC + Spring Security for REST API
  - Spring GraphQL 1.4.1 + graphql-java 21.0 for GraphQL API
  - Spring Data JPA for persistence
- **Key libraries:**
  - **CQRS buses** (from org.albertsanso):
    - commandbus-synchronous-inmemory (command dispatch)
    - querybus-synchronous-inmemory (query dispatch)
    - eventbus-synchronous-inmemory (event publishing)
    - commons-core (shared utilities)
  - **Data access:** Hibernate/JPA, HikariCP connection pooling
  - **JSON serialization:** Jackson (jackson-databind 2.19.4)
  - **API documentation:** springdoc-openapi 2.0.4 + Swagger UI
  - **Security:** Spring Security 6.5.5, JJWT 0.12.5 (JWT tokens)
  - **Utils:** Lombok 1.18.42 (code generation)
- **Database support:** PostgreSQL 42.7.8 (primary) + MySQL 9.4.0 (fallback)
- **Validation:** Spring Boot Validation starter
- **Encoding:** UTF-8 throughout

## Module dependency graph

```
tt-data-league-api-runtime
  ├─→ tt-data-league-api-rest
  ├─→ tt-data-league-api-graphql
  ├─→ tt-data-league-core-domain (external)
  ├─→ tt-data-league-core-repository-jpa (external)
  └─→ Albert Sanso buses (commons-core, commandbus, querybus, eventbus)

tt-data-league-api-rest
  ├─→ tt-data-league-api-core
  ├─→ tt-data-league-core-domain (external)
  └─→ Spring Web, Jackson, springdoc-openapi, JWT

tt-data-league-api-graphql
  ├─→ tt-data-league-api-core
  ├─→ tt-data-league-core-domain (external)
  ├─→ tt-data-league-core-repository-jpa (external)
  └─→ Spring GraphQL, graphql-java, QueryBus

tt-data-league-api-core
  ├─→ tt-data-league-core-domain (external)
  ├─→ tt-data-league-core-repository-jpa (external)
  └─→ Albert Sanso buses (commons-core, commandbus, querybus, eventbus)

tt-data-league-api-repository-jpa
  ├─→ tt-data-league-core-domain (external)
  ├─→ tt-data-league-core-repository-jpa (external)
  ├─→ Spring Data JPA, Hibernate
  └─→ Albert Sanso buses (commons-core, commandbus, querybus, eventbus)
```

## Global build & test commands

```bash
# Build entire project (all modules)
mvn clean install -DskipTests

# Build and run all tests
mvn clean install

# Build a single module and its dependencies
mvn -pl tt-data-league-api-<module-name> -am clean install

# Run all tests across all modules
mvn test

# Run tests for a specific module
mvn -pl tt-data-league-api-<module-name> test

# Run REST API integration tests only
mvn -pl tt-data-league-api-rest test

# Quick compile check (no tests)
mvn clean compile

# Verify build without installing to local repo
mvn clean verify -DskipTests

# Regenerate inferred OpenAPI contract from source
python scripts/regenerate_openapi.py

# Quick OpenAPI structural check
python scripts/verify_openapi.py

# Validate OpenAPI file with swagger-cli
swagger-cli validate openapi.yaml

# Check for dependency updates
mvn versions:display-dependency-updates
```

## Code style & static analysis

- **Formatter:** None explicitly configured (use IDE default or submit PRs matching existing style).
- **Checkstyle/PMD/SpotBugs:** Not configured; no linting enforced at build time.
- **Lombok:** Used in multiple modules (`@Data`, `@Getter`, `@Setter`, `@RequiredArgsConstructor`). Do not manually write equals/hashCode/toString on classes annotated with Lombok.
- **Annotation processors:** JPA entity enhancement may occur; check `target/generated-sources/` for processed classes.

## Global coding conventions

- **Package naming:** `org.cttelsamicsterrassa.data.api.<module-name>.<layer>` (e.g., `org.cttelsamicsterrassa.data.api.rest.club`)
- **Exception hierarchy:** Use standard Spring exceptions and domain-specific exceptions from core-domain module.
- **Logging:** SLF4J via Logback (auto-configured by Spring Boot). Log via `log.info()`, `log.error()`, etc. (use Lombok's `@Slf4j` annotation or `LoggerFactory.getLogger()`).
- **Transactions:** `@Transactional` only on service layer methods that modify state. Query-only methods may omit it.
- **Null safety:** Use `@NonNull` from Spring Framework or `java.util.Optional` for nullable returns. No explicit null checks needed for `@NonNull` parameters (Spring validates at runtime if configured).
- **Thread safety:** Services are assumed thread-safe (stateless). Repositories are thread-safe (managed by Spring/Hibernate).

## Testing conventions

- **Framework:** JUnit 5 (provided by spring-boot-starter-test).
- **Mocking:** Mockito (standard in spring-boot-starter-test).
- **Integration tests:** Use `@SpringBootTest` + Testcontainers for PostgreSQL (auto-start if present).
- **Test naming:** `<ClassName>Test` for unit tests, `<ClassName>IntegrationTest` for integration tests.
- **Base test classes:** None mandated; extend `@SpringBootTest` as needed.
- **Code coverage threshold:** Not enforced; Jacoco not configured.
- **Test data:** Use SQL fixtures in `src/test/resources/` or seed via `@sql` annotations.

## CI/CD context

- **Pipeline:** GitHub Actions workflow included for OpenAPI validation (`.github/workflows/validate-openapi.yaml`).
- **Branch strategy:** Trunk-based development assumed (main/master is stable).
- **Quality gates:** Changes to `openapi.yaml` must pass `swagger-cli validate` in CI; pull requests should also pass `mvn clean install` (build + tests).
- **Configuration file location:** GitHub workflow files live in `.github/workflows/`.

## Files and areas agents must never modify

- `target/` directory — contains compiled classes and generated sources; deleted by `mvn clean`.
- Files under `target/generated-sources/annotations/` — auto-generated by JPA/Lombok annotation processors.
- `.mvn/wrapper/` — Maven Wrapper configuration; updates via `mvn wrapper:wrapper` only.
- `pom.xml` version strings — bump only via explicit version management (e.g., `mvn versions:set`).
- `openapi.yaml` — inferred/generated contract; regenerate via `scripts/regenerate_openapi.py` and validate instead of manual broad rewrites.
- Auto-generated SQL or schema files (if any) in `src/main/resources/db/` — mark with comments.

## External systems and contracts

- **Databases:**
  - PostgreSQL runtime defaults: `localhost:15432` with user `guest`/password `guest` (see `tt-data-league-api-runtime/src/main/resources/application.properties`).
  - Local Docker sample (`docker/docker-compose.yml`): PostgreSQL on `localhost:5432` with DB/user/password `mydb`/`compose-postgres`/`compose-postgres` (override `SPRING_DATASOURCE_*` to use it from runtime).
  - MySQL (fallback): commented out in `application.properties` but available for testing.
  - Schema management: `spring.jpa.hibernate.ddl-auto=update` (auto-create/update on startup in dev; use Flyway or Liquibase for production).

- **Message brokers:** None currently integrated; event bus is in-memory only (sync dispatch).

- **External APIs:**
  - tt-data-league-core-domain and tt-data-league-core-repository-jpa are imported as Maven artifacts (managed in separate repository).
  - Document the external project repository URL here: `<!-- TODO: Add link to tt-data-league-core repository -->`

## Glossary

| Term | Definition |
|---|---|
| **Club** | A table tennis club entity with members across multiple seasons. |
| **Practitioner** | A table tennis player (practitioner of the sport). |
| **Season** | A time period (year range) during which matches occur. |
| **Match** | A game result between players/teams with detailed statistics. |
| **SeasonPlayer** | A player registered for a particular season, with results linked. |
| **CQRS** | Command Query Responsibility Segregation — separates write (Commands) and read (Queries) models. |
| **CommandBus** | Synchronous dispatcher for write operations (handled by command handlers). |
| **QueryBus** | Synchronous dispatcher for read operations (handled by query handlers). |
| **EventBus** | Synchronous dispatcher for domain events (handled by event subscribers). |

## Notes for agents

- Always read the module-specific `AGENTS.md` before modifying code in that module.
- When adding new REST endpoints, update the OpenAPI/Swagger documentation via `springdoc-openapi` annotations.
- When adding GraphQL queries/mutations, update `src/main/resources/graphql/schema.graphqls` first.
- Database dialect is configurable; keep both PostgreSQL and MySQL dialects in properties (commented out), for flexibility.

