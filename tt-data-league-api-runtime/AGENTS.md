# AGENTS.md — tt-data-league-api-runtime

> Inherits global context from [root AGENTS.md](../AGENTS.md).

## Module purpose

The main Spring Boot application that bootstraps the entire system. This module aggregates the REST and GraphQL API layers, configures application-wide concerns (security, database connectivity, transaction management), and provides the executable JAR entry point. It is the only runnable service in this monolith.

## Architecture overview

- **Type:** Runnable Spring Boot application (`@SpringBootApplication` main class)
- **Configuration:** Spring Security (JWT-based), database connection pooling (HikariCP), transaction management
- **Server:** Embeds Tomcat; listens on port 8080 for HTTP requests, port 9091 for management/metrics
- **Startup:** Auto-configures REST controllers, GraphQL resolvers, JPA repositories, and CQRS buses
- **Package structure:**
  - `org.cttelsamicsterrassa.data.api.runtime.config` — Spring configuration beans
  - `org.cttelsamicsterrassa.data.api.runtime.config.security` — JWT, user details, authentication filters
  - `org.cttelsamicsterrassa.data.api.rest.auth` — authentication controller (login endpoint)

## Entry points

| Class | Role |
|---|---|
| `APIApplication.java` | Main class with `public static void main()` and `@SpringBootApplication` |
| `AppConfig.java` | Spring configuration class defining beans (buses, repositories, etc.) |
| `SecurityConfig.java` | Spring Security configuration (JWT filters, authorization rules, CORS) |
| `JwtAuthenticationFilter.java` | Custom filter that validates JWT tokens on every request |
| `JwtService.java` | Generates and validates JWT tokens |
| `AuthController.java` | REST endpoint at `/api/v1/auth/login` for user authentication |
| `TokenBlacklistService.java` | Manages token revocation (logout) |

## Module dependencies

**Internal:**
- `tt-data-league-api-rest` — REST controllers and Swagger configuration
- `tt-data-league-api-graphql` — GraphQL resolvers and schema
- `tt-data-league-api-core` — business logic (command/query handlers)
- `tt-data-league-api-repository-jpa` — JPA repository implementations

**External:**
- Spring Boot Starters: web, data-jpa, security, jdbc
- Spring Security, JJWT (JWT token handling)
- Hibernate/JPA, HikariCP, PostgreSQL + MySQL drivers
- Albert Sanso buses (commons-core, commandbus, querybus, eventbus)

## Build & test commands

```bash
# From repo root: build entire project (runtime + all dependencies)
mvn clean install -DskipTests

# Build and test entire project
mvn clean install

# Build runtime module only (includes dependencies)
mvn -pl tt-data-league-api-runtime -am clean install

# Run Spring Boot application (from repo root)
mvn -pl tt-data-league-api-runtime spring-boot:run

# Package executable JAR
mvn -pl tt-data-league-api-runtime clean package

# Run executable JAR
java -jar tt-data-league-api-runtime/target/tt-data-league-api-runtime-0.0.1-SNAPSHOT.jar
```

## Configuration

| Property | Default | Purpose | Env Override |
|---|---|---|---|
| `spring.application.name` | `tt-data-league-importer` | Application identifier | `SPRING_APPLICATION_NAME` |
| `management.server.port` | `9091` | Metrics/health endpoint port | `MANAGEMENT_SERVER_PORT` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:15432/ttleaguedata` | PostgreSQL JDBC URL | `SPRING_DATASOURCE_URL` |
| `spring.datasource.username` | `guest` | DB username (dev only) | `SPRING_DATASOURCE_USERNAME` |
| `spring.datasource.password` | `guest` | DB password (dev only) | `SPRING_DATASOURCE_PASSWORD` |
| `spring.jpa.hibernate.ddl-auto` | `update` | Auto-create/alter schema | `SPRING_JPA_HIBERNATE_DDL_AUTO` |
| `spring.jpa.properties.hibernate.dialect` | `PostgreSQLDialect` | Database dialect | (change in properties) |
| `spring.datasource.hikari.maximum-pool-size` | `10` | HikariCP pool size | `HIKARI_MAXIMUM_POOL_SIZE` |
| `spring.datasource.hikari.connection-timeout` | `20000` | Connection wait time (ms) | `HIKARI_CONNECTION_TIMEOUT` |
| `management.endpoints.web.exposure.include` | `*` | Actuator endpoints (dev only; restrict in prod) | — |

**Database switching:**
- PostgreSQL (current, configured): Keep `spring.jpa.properties.hibernate.dialect=PostgreSQLDialect`
- MySQL (commented out): Uncomment MySQL URL/dialect lines, comment out PostgreSQL lines

**Security (TODO for production):**
- JWT secret key: Currently hardcoded in `JwtService`; move to environment variable or Vault
- User repository: Currently in-memory (see `UserRepo`); integrate with database in production

**All config in:** `src/main/resources/application.properties`

## Module-specific coding conventions

- **Main class:** Single `APIApplication.java` with `public static void main(String[] args)`
- **Configuration classes:** Use `@Configuration` + `@Bean` methods in `config/` package
- **Security filters:** Implement `OncePerRequestFilter` and register via `SecurityConfig`
- **Token validation:** `JwtAuthenticationFilter` runs before Spring Security's filter chain; validates JWT and populates `SecurityContext`
- **Controller paths:** All REST at `/api/v1/**`, GraphQL at `/graphql`, auth at `/api/v1/auth/**`
- **Exception handling:** Global `@ControllerAdvice` (if present) or per-controller `@ExceptionHandler`

## Constraints and fragile areas

- **No business logic here:** All handlers are in the core module. Runtime is config + aggregation only.
- **Environment variables in production:** Database URL, JWT secret, and passwords MUST come from environment or Vault, not `application.properties`.
- **Circular dependencies:** Ensure no module imports runtime module (unidirectional dependency from runtime → all others).
- **Database migration:** Currently using Hibernate auto-schema (fragile). For production, use Flyway or Liquibase with versioned migration scripts.
- **Token blacklist:** `TokenBlacklistService` is in-memory. For distributed deployments, use Redis or database-backed revocation.
- **Port conflicts:** Ports 8080 (HTTP) and 9091 (management) must be available; override via `server.port` and `management.server.port`.

## Testing strategy

- **Integration tests:** Full `@SpringBootTest` with `TestRestTemplate` or GraphQL test client; requires database (Testcontainers auto-starts PostgreSQL).
- **Security tests:** Test JWT validation, unauthorized requests, token expiry
- **Example integration test:**
  ```java
  @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
  class APIApplicationIntegrationTest {
      @Autowired private TestRestTemplate restTemplate;
      @LocalServerPort int port;
      
      @Test
      void shouldReturnUnauthorizedWithoutToken() {
          ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/api/v1/clubs", String.class);
          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
      }
  }
  ```

## Running locally

1. **Start PostgreSQL** (via Docker Compose in `docker/`):
   ```bash
   docker-compose -f docker/docker-compose.yml up -d
   ```

2. **Run Spring Boot:**
   ```bash
   mvn clean install -DskipTests
   mvn -pl tt-data-league-api-runtime spring-boot:run
   ```

3. **Access services:**
   - REST API: `http://localhost:8080/swagger-ui.html`
   - GraphQL: `http://localhost:8080/graphiql`
   - Metrics: `http://localhost:9091/actuator`

## Related context

- [Root AGENTS.md](../AGENTS.md)
- `src/main/resources/application.properties` — all configuration
- `docker/docker-compose.yml` — PostgreSQL setup for local development
- [Spring Security with JWT](https://www.baeldung.com/spring-security-authentication-with-jwt) (external reference)
- [Spring Boot Configuration](https://spring.io/projects/spring-boot)

