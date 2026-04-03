# AGENTS.md — tt-data-league-api-rest

> Inherits global context from [root AGENTS.md](../AGENTS.md).

## Module purpose

The REST API layer providing HTTP endpoints for client consumption. This module defines Spring MVC controllers, REST DTOs, OpenAPI/Swagger documentation, and request/response mappings. It delegates business logic to the core module via CommandBus and QueryBus, and handles serialization/deserialization via Jackson.

## Architecture overview

- **Framework:** Spring MVC (via spring-boot-starter-web)
- **API versioning:** RESTful endpoints at `/api/v1/**`
- **Documentation:** OpenAPI 3.0 via `springdoc-openapi-starter-webmvc-ui` (Swagger UI at `/swagger-ui.html`)
- **Security:** JWT token validation (via `JwtAuthenticationFilter` in runtime module)
- **Package structure:**
  - `org.cttelsamicsterrassa.data.api.rest.<entity>/` — controllers and DTOs per domain entity
  - `org.cttelsamicsterrassa.data.api.rest.shared/` — shared DTOs (e.g., `CompetitionInfoDto`)
  - `org.cttelsamicsterrassa.data.api.rest.error/` — error handling and exception mappers

## Entry points

| Class | Role |
|---|---|
| `*Controller` (e.g., `ClubController`) | Spring `@RestController` handling HTTP `GET`, `POST`, `PUT`, `DELETE` at `/api/v1/clubs/**` |
| `*Dto` / `*Request` / `*Response` | Jackson-serializable request/response payloads (immutable or Lombok `@Data`) |
| `OpenApiConfig.java` | Configures Swagger/OpenAPI metadata (title, version, security schemes) |
| `ControllerConfig.java` | API version constants and base paths |
| `OpenApiIntegrationTest.java` | Tests OpenAPI endpoint availability and schema validity |

## Module dependencies

**Internal:**
- `tt-data-league-api-core` — for CommandBus, QueryBus, business handlers
- `tt-data-league-core-domain` (external) — domain entities, commands, queries

**External:**
- Spring Web, Spring Security
- Jackson (JSON serialization)
- springdoc-openapi 2.0.4 (automatic OpenAPI spec generation)
- JJWT (JWT token parsing in security filters)
- Spring Boot Validation (for `@Valid`, `@NotNull`, etc.)
- Lombok

## Build & test commands

```bash
# From repo root: build REST module only
mvn -pl tt-data-league-api-rest -am clean install

# Run REST integration tests (tests controllers + OpenAPI generation)
mvn -pl tt-data-league-api-rest test

# Compile only
mvn -pl tt-data-league-api-rest clean compile
```

## Configuration

| Property | Source | Description |
|---|---|---|
| `springdoc.api-docs.path` | `application.properties` | OpenAPI JSON endpoint (`/v3/api-docs`) |
| `springdoc.swagger-ui.path` | `application.properties` | Swagger UI path (`/swagger-ui.html`) |
| `springdoc.api-docs.title` | `application.properties` | API title in spec |
| `springdoc.api-docs.version` | `application.properties` | API version in spec |

Swagger UI is auto-enabled in this module and available at `http://localhost:8080/swagger-ui.html` (once runtime is running).

## Module-specific coding conventions

- **Controller naming:** `<Entity>Controller` (e.g., `ClubController`)
- **DTO naming:** `<Entity>Dto`, `<Entity>Request`, `<Entity>Response` depending on usage
- **Endpoint paths:** `/api/v1/<resource>` (RESTful style, e.g., `/api/v1/clubs`, `/api/v1/clubs/{id}`)
- **HTTP methods:** Follow REST conventions:
  - `GET /api/v1/clubs` — list all
  - `GET /api/v1/clubs/{id}` — get one by ID
  - `POST /api/v1/clubs` — create (returns 201 Created)
  - `PUT /api/v1/clubs/{id}` — update (idempotent)
  - `DELETE /api/v1/clubs/{id}` — delete (returns 204 No Content)
- **OpenAPI annotations:** Use `@Operation`, `@ApiResponse`, `@Parameter` from `springdoc-openapi` on controller methods to document each endpoint.
- **Error responses:** Return appropriate HTTP status codes (400, 404, 409, 500); exception mappers in `error/` package translate domain exceptions.
- **DTO to Command/Query mapping:** Controllers convert request DTOs → command objects → CommandBus, and query result DTOs ← QueryBus responses.

## Constraints and fragile areas

- **OpenAPI schema relationship:** The `openapi.yaml` file at repo root is auto-generated from controller annotations. Do NOT manually edit it; regenerate via `mvn clean generate-resources` or the script in `scripts/regenerate_openapi.py`.
- **JWT token validation:** Tokens are validated by `JwtAuthenticationFilter` in the runtime module, not here. Controllers can assume `@AuthenticationPrincipal` is populated if behind security.
- **No business logic in controllers:** Controllers must only:
  1. Validate input (via Spring Validation)
  2. Map to command/query objects
  3. Dispatch to bus
  4. Map result to response DTO
5. Return HTTP response
- **Circular DTO references:** Avoid deep nesting of DTOs (e.g., Club → ClubMembers → Practitioners); consider flattening or separate endpoints for details.

## Testing strategy

- **Unit tests:** Test controller input validation and response mapping using `@WebMvcTest` and MockMvc.
- **Integration tests:** Test full request/response cycle with real or stubbed CommandBus/QueryBus.
- **OpenAPI tests:** `OpenApiIntegrationTest` verifies that the OpenAPI spec is generated and reachable at `/v3/api-docs`.
- **Example unit test:**
  ```java
  @WebMvcTest(ClubController.class)
  class ClubControllerTest {
      @Autowired private MockMvc mockMvc;
      @MockBean private CommandBus commandBus;
      @MockBean private QueryBus queryBus;
      
      @Test
      void shouldGetClubByIdAndReturn200() throws Exception {
          // Setup query handler mock
          // mockMvc.perform(get("/api/v1/clubs/1"))
          //     .andExpect(status().isOk())
      }
  }
  ```

## Related context

- [Root AGENTS.md](../AGENTS.md)
- `src/main/resources/application.properties` — Swagger configuration
- `docs/openapi_readme.md` — OpenAPI generation details
- `scripts/regenerate_openapi.py` — regenerate openapi.yaml after controller changes
- [Springdoc-OpenAPI docs](https://springdoc.org/)

