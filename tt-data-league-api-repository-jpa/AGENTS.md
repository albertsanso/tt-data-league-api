# AGENTS.md — tt-data-league-api-repository-jpa

> Inherits global context from [root AGENTS.md](../AGENTS.md).

## Module purpose

The data access layer providing JPA/Hibernate implementations of repository interfaces defined in `tt-data-league-core-repository-jpa`. This module owns all direct database interaction, entity mapping, and query logic. It translates domain entities to/from JPA entities and performs CRUD operations.

## Architecture overview

- **Pattern:** Repository pattern (Data Mapper)
- **Framework:** Spring Data JPA + Hibernate
- **Package structure:**
  - `org.cttelsamicsterrassa.data.api.repository.jpa` — JPA entity implementations of repository interfaces
  - `org.cttelsamicsterrassa.data.api.repository.shared` — shared JPA concerns (base classes, listeners, etc.)
- **Entity layering:** Domain entities from `core-domain` are mapped to JPA `@Entity` classes in this module.

## Entry points

| Class / Component | Role |
|---|---|
| Repository interfaces (from `core-repository-jpa`) | Defined externally; implementations are Spring Data `JpaRepository` subclasses here |
| JPA `@Entity` classes | Map domain objects to database tables (one entity per main domain type) |
| `@Configuration` class (if present) | JPA/Hibernate overrides, custom repository factory beans |

## Module dependencies

**Internal:**
- `tt-data-league-core-domain` (external) — domain entity contracts
- `tt-data-league-core-repository-jpa` (external) — repository interfaces to implement

**External:**
- Spring Boot Data JPA starter
- Hibernate (included in spring-boot-starter-data-jpa)
- HikariCP connection pool
- Albert Sanso buses (for event publishing if entity listeners emit events)
- Lombok (for entity `@Data` / `@Getter` / `@Setter` generation)

## Build & test commands

```bash
# From repo root: build repository module only
mvn -pl tt-data-league-api-repository-jpa -am clean install

# Test repository module (requires PostgreSQL or Testcontainers)
mvn -pl tt-data-league-api-repository-jpa test

# Compile only
mvn -pl tt-data-league-api-repository-jpa clean compile
```

**Environment for testing:**
- Requires PostgreSQL running on `localhost:15432` (or override via `SPRING_DATASOURCE_URL`)
- Or Testcontainers will auto-start PostgreSQL if on classpath (auto-managed by test runner)

## Configuration

| Property | Default | Purpose |
|---|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:15432/ttleaguedata` | JDBC connection URL |
| `spring.datasource.username` | `guest` | DB user (dev only) |
| `spring.datasource.password` | `guest` | DB password (dev only) |
| `spring.jpa.hibernate.ddl-auto` | `update` | Auto-create/alter schema on startup |
| `spring.jpa.properties.hibernate.dialect` | `PostgreSQLDialect` | Or `MySQL8Dialect` if switching DB |
| `spring.datasource.hikari.maximum-pool-size` | `10` | HikariCP pool size |
| `spring.datasource.hikari.minimum-idle` | `5` | Minimum idle connections |

(These are set in the runtime module's `application.properties` but are used by this module's tests.)

## Module-specific coding conventions

- **Entity naming:** `<EntityName>Entity` (e.g., `ClubEntity`, `MatchEntity`)
- **Repository interface suffixes:** Repository implementations extend `JpaRepository<Entity, ID>` and **must not** add custom logic (keep all filtering/mapping in service layer)
- **Lombok usage:** Use `@Data` on JPA entities for getters/setters, but be aware of:
  - Avoid `@Data` on entities with `@OneToMany` or `@ManyToMany` (use explicit `@Getter` + `@Setter` instead to control equals/hashCode)
  - Suppress `equals()` and `hashCode()` for circular references: `@EqualsAndHashCode(exclude = "relatedEntity")`
- **No business logic in entities:** Entities are anemic; all transformation logic lives in handlers or services.
- **Transactions:** Do NOT use `@Transactional` here; transactions are managed at the service layer (core module or runtime).

## Constraints and fragile areas

- **Schema ownership:** Once a table is created, changes to entity mapping require manual migration planning or Flyway/Liquibase scripts (currently using Hibernate's `ddl-auto=update` in dev, which is fragile).
- **No direct HTTP/GraphQL:** Repository module must not reference Spring Web or Spring GraphQL.
- **Lazy loading gotchas:** Be careful with Hibernate lazy loading; fetch eagerly in repository queries if accessed outside a transaction.
- **No hardcoded SQL:** Use JPA query methods or `@Query` annotations; avoid JDBC directly.

## Testing strategy

- **Unit tests:** Test repository finder methods with mock data or embedded H2 database (setup via `@DataJpaTest`).
- **Integration tests:** Use `@SpringBootTest` + Testcontainers (PostgreSQL) to test full entity lifecycle.
- **Example unit test:**
  ```java
  @DataJpaTest
  class ClubEntityRepositoryTest {
      @Autowired private JpaRepository<ClubEntity, Long> repo;
      
      @Test
      void shouldSaveAndFindClub() {
          ClubEntity club = new ClubEntity();
          club.setName("Test Club");
          repo.save(club);
          
          Optional<ClubEntity> found = repo.findById(club.getId());
          assertThat(found).isPresent();
      }
  }
  ```

## Related context

- [Root AGENTS.md](../AGENTS.md)
- [Spring Data JPA documentation](https://spring.io/projects/spring-data-jpa)
- `src/main/resources/db/` — any manual SQL migration scripts (if Flyway/Liquibase added in future)

