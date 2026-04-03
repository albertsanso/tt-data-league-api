# AGENTS.md — tt-data-league-api-graphql

> Inherits global context from [root AGENTS.md](../AGENTS.md).

## Module purpose

The GraphQL API layer providing a graph-based query interface for client consumption. This module defines Spring GraphQL resolvers, GraphQL schema definitions, and query/mutation handlers. It delegates business logic to the core module via the QueryBus, and optionally handles mutations through CommandBus integration.

## Architecture overview

- **Framework:** Spring GraphQL 1.4.1 + graphql-java 21.0
- **Schema definition:** GraphQL schema in `src/main/resources/graphql/schema.graphqls`
- **Resolvers:** Spring GraphQL `@Controller` beans with `@QueryMapping` and `@MutationMapping` methods
- **Documentation:** GraphiQL web UI at `/graphiql` (auto-enabled)
- **CORS:** Configured to allow cross-origin requests from any client
- **Package structure:**
  - `org.cttelsamicsterrassa.data.api.graphql.<entity>/` — resolvers per domain entity
  - `org.cttelsamicsterrassa.data.api.graphql.config/` — GraphQL configuration beans

## Entry points

| Component | Role |
|---|---|
| `src/main/resources/graphql/schema.graphqls` | GraphQL schema: query types, mutations, input types, and type definitions |
| `*Resolver` classes (e.g., `ClubResolver`) | Spring `@Controller` beans with `@QueryMapping` and `@MutationMapping` methods |
| `GraphQLConfig.java` (if present) | Custom beans for scalar types, exception handling, or WebSocket subscriptions |
| GraphiQL UI | Available at `http://localhost:8080/graphiql` for interactive query testing |

## Module dependencies

**Internal:**
- `tt-data-league-api-core` — for QueryBus and command handlers
- `tt-data-league-core-domain` (external) — domain entities and query contracts
- `tt-data-league-core-repository-jpa` (external) — repository interfaces (for resolvers to query data)

**External:**
- Spring GraphQL, graphql-java
- querybus-synchronous-inmemory (for query dispatch)
- commons-core (shared utilities)
- Spring Boot Web (for HTTP transport)
- Lombok

## Build & test commands

```bash
# From repo root: build GraphQL module only
mvn -pl tt-data-league-api-graphql -am clean install

# Run tests (GraphQL resolver tests + integration tests)
mvn -pl tt-data-league-api-graphql test

# Compile only
mvn -pl tt-data-league-api-graphql clean compile
```

## Configuration

| Property | Default | Purpose |
|---|---|---|
| `spring.graphql.graphiql.enabled` | `true` | Enable GraphiQL web UI |
| `spring.graphql.graphiql.path` | `/graphiql` | GraphiQL endpoint |
| `spring.graphql.schema.printer.enabled` | `true` | Allow inspection of schema at `/graphql/schema` |
| `spring.graphql.cors.allowed-origins` | `*` | Allow any origin to query GraphQL endpoint |
| `spring.graphql.cors.allowed-methods` | `GET,POST,OPTIONS` | Allowed HTTP methods |
| `spring.graphql.cors.max-age` | `1800` | CORS cache max age (seconds) |

All set in `src/main/resources/application.properties`.

## Module-specific coding conventions

- **Resolver class naming:** `<Entity>Resolver` (e.g., `ClubResolver`)
- **Schema file naming:** Single file `schema.graphqls` at `src/main/resources/graphql/`
- **Query/Mutation/Subscription organization:**
  - `type Query { }` — read-only operations (queries to QueryBus)
  - `type Mutation { }` — write operations (commands to CommandBus, returns query result)
  - `type Subscription { }` — (not currently used; event bus is synchronous)
- **Resolver method annotations:**
  - `@QueryMapping(name = "queryName")` — maps GraphQL field to resolver method
  - `@MutationMapping` — maps mutation field
  - `@SchemaMapping(typeName = "Type", field = "fieldName")` — field resolver on a type
- **Input types:** Define GraphQL `input` types in schema for complex mutations (e.g., `CreateClubInput`)
- **Return type mapping:** Resolver methods return domain entities; graphql-java and Spring GraphQL auto-map to schema types.

## Constraints and fragile areas

- **Schema ownership:** The `schema.graphqls` file must be kept in sync with resolver methods. Any new query/mutation must be added to the schema first, then implemented in a resolver.
- **No direct HTTP:** Resolvers must not reference Spring MVC or servlet APIs; use Spring GraphQL's built-in context and request objects only.
- **Eager loading in resolvers:** Be careful with lazy-loaded relationships; consider using `@EntityGraph` in repository methods or eager fetching to avoid N+1 queries.
- **GraphQL error handling:** Exceptions thrown in resolvers are caught by Spring GraphQL and converted to GraphQL error responses; ensure they include meaningful error messages.
- **No circular references in schema:** GraphQL schema does not support circular types; flatten relationships or use fragments on the client side.

## Testing strategy

- **Unit tests:** Test individual resolver methods with mocked QueryBus using `@GraphQlTest` (provided by spring-graphql).
- **Integration tests:** Use `@SpringBootTest` + a GraphQL test client to execute full queries against the schema.
- **Schema validation:** Ensure schema is syntactically correct by running tests (any schema errors are caught at startup).
- **Example resolver test:**
  ```java
  @GraphQlTest(ClubResolver.class)
  class ClubResolverTest {
      @MockBean private QueryBus queryBus;
      private GraphQlTester graphQlTester;
      
      @Test
      void shouldQueryClubById() {
          graphQlTester.document("""
              query {
                findClubById(id: "1") {
                  id
                  name
                }
              }
              """)
              .execute()
              .path("data.findClubById.name").entity(String.class).isEqualTo("Test Club");
      }
  }
  ```

## Related context

- [Root AGENTS.md](../AGENTS.md)
- `src/main/resources/graphql/schema.graphqls` — GraphQL type definitions
- `src/main/resources/application.properties` — GraphQL configuration
- [Spring GraphQL documentation](https://spring.io/projects/spring-graphql)
- [GraphQL Best Practices](https://graphql.org/learn/best-practices/)

