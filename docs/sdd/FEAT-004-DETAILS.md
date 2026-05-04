# FEAT-004 — Adapt for Roles and Permissions for User Management

## Build Plan

> Status transitions: `idea` → `planned` → `ready` → `in-progress` → `done`
> Update FEATURES.md status as work progresses.

---

## Context — current state

The existing auth stack uses `AuthenticationService` with a 3-argument constructor
(`UserRepository`, `PasswordHasher`, `UserValidator`) and `UserPrincipal.getAuthorities()`
returns a single hardcoded `"USER"` authority regardless of who is logged in.

The upstream external artifacts have been updated:

- **`tt-data-league-core-domain`** now exposes a full RBAC model:
  `Role`, `Permission`, `Resource` (enum), `PermissionAction` (enum), `RbacCatalog`,
  `AuthorizationException`, `RoleRepository`, `PermissionRepository`.
- **`tt-data-league-core-repository-jpa`** now ships JPA wiring for all new entities:
  `RoleRepositoryImpl`, `PermissionRepositoryImpl`, mappers, helpers, and JPA models
  (`RoleJPA`, `PermissionJPA`).
- **`AuthenticationService`** constructor signature has changed — it now requires a
  `RoleRepository` as its second parameter. The current `AuthConfig` bean definition
  **will not compile** against the new artifacts.

The plan below is broken into atomic, ordered steps that restore compilation first,
then progressively add role/permission capabilities.

---

## External API reference (do not re-declare in this repo)

### `tt-data-league-core-domain` — relevant classes

| Class / Interface | Package | Key API |
|---|---|---|
| `User` | `model.auth` | `getRoles()`, `assignRole(Role)`, `hasRole(String)`, `hasPermission(Resource, PermissionAction)`, `createExisting(…, Set<Role>)` |
| `Role` | `model.auth` | `createNew(name, permissions)`, `createExisting(id, name, permissions)`, `getName()`, `getPermissions()`, `addPermission(Permission)`, `hasPermission(Resource, PermissionAction)` |
| `Permission` | `model.auth` | `createNew(Resource, PermissionAction)`, `createExisting(id, Resource, PermissionAction)`, `getResource()`, `getAction()` |
| `PermissionAction` | `model.auth` | enum: `READ`, `WRITE`, `DELETE` |
| `Resource` | `model.auth` | enum: `CLUB`, `PRACTITIONER`, `CLUB_MEMBER`, `SEASON_PLAYER`, `SEASON_PLAYER_RESULT`, `MATCH` |
| `RoleRepository` | `repository.auth` | `findById`, `findByName`, `findAll`, `existsByName`, `save`, `saveAll`, `deleteById` |
| `PermissionRepository` | `repository.auth` | `findById`, `findByResourceAndAction`, `findAll`, `save`, `saveAll`, `deleteById` |
| `RbacCatalog` | `service.auth` | constants `ADMIN`, `CLUB_MANAGER`, `PRACTITIONER`, `GUEST`, `ANALYST`; `defaultRoleName()` → `GUEST`; `predefinedRoles()` → `List<Role>` pre-built with their permissions |
| `AuthorizationException` | `service.auth` | Thrown by `AuthenticationService.assertAuthorized(…)` |
| `AuthenticationService` | `service.auth` | Constructor: `(UserRepository, RoleRepository, PasswordHasher, UserValidator)` · New methods: `assignRole(UUID, String)`, `userHasPermission(UUID, Resource, PermissionAction)`, `assertAuthorized(UUID, Resource, PermissionAction)`, `getUserByUsername(String)`, `getUserByEmail(String)`, `disableUser(UUID)`, `enableUser(UUID)`, `changeUserPassword(UUID, String)` |

### `tt-data-league-core-repository-jpa` — relevant classes

| Class | Constructor | Notes |
|---|---|---|
| `PermissionJPAToPermissionMapper` | `()` — no-arg | Converts `PermissionJPA` → `Permission` |
| `PermissionToPermissionJPAMapper` | `()` — no-arg | Converts `Permission` → `PermissionJPA` |
| `RoleJPAToRoleMapper` | `(PermissionJPAToPermissionMapper)` | Converts `RoleJPA` → `Role` |
| `RoleToRoleJPAMapper` | `(PermissionToPermissionJPAMapper)` | Converts `Role` → `RoleJPA` |
| `PermissionRepositoryHelper` | Spring Data JPA interface — auto-created | Has `findByResourceAndAction` |
| `PermissionRepositoryImpl` | `(PermissionRepositoryHelper, PermissionJPAToPermissionMapper, PermissionToPermissionJPAMapper)` | Implements `PermissionRepository` |
| `RoleRepositoryHelper` | Spring Data JPA interface — auto-created | Has `findByNameIgnoreCase`, `existsByNameIgnoreCase` |
| `RoleRepositoryImpl` | `(RoleRepositoryHelper, RoleJPAToRoleMapper, RoleToRoleJPAMapper)` | Implements `RoleRepository` |
| `UserRepositoryImpl` | `(UserRepositoryHelper, UserJPAToUserMapper, UserToUserJPAMapper)` | Constructor unchanged; now maps user roles via updated mappers |

---

## Step 1 — Compile-check to confirm current breakage

Run the full compile to confirm `AuthConfig` fails due to the changed constructor:

```bash
mvn -pl tt-data-league-api-rest -am clean compile
```

Expected failure: `AuthenticationService` constructor call in `AuthConfig` uses 3 args;
the external artifact requires 4. Fix this in Step 2.

---

## Step 2 — Fix `AuthConfig.java`: add Role/Permission beans and fix constructor

**File:** `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/config/security/AuthConfig.java`

Changes:
1. Add beans for the two no-arg permission mappers.
2. Add beans for the two role mappers (each accepts one permission mapper).
3. Add `PermissionRepository` bean via `PermissionRepositoryImpl`.
4. Add `RoleRepository` bean via `RoleRepositoryImpl`, injecting the role mapper beans and `RoleRepositoryHelper`.
5. Fix `authenticationService` bean: add `RoleRepository` as the second constructor argument.

> **Dependency chain to respect:**
> `PermissionJPAToPermissionMapper` (no-arg)
> → `RoleJPAToRoleMapper(permJpaToPermMapper)`
> → `RoleRepositoryImpl(helper, jpaToRoleMpr, roleToJpaMpr)`
> → `AuthenticationService(userRepo, roleRepo, passwordHasher, userValidator)`

Wire `PermissionRepositoryHelper` and `RoleRepositoryHelper` via Spring auto-detection:
both are `JpaRepository` interfaces already covered by `@EnableJpaRepositories("org.cttelsamicsterrassa")`.

After this step: `mvn -pl tt-data-league-api-rest -am clean compile` must succeed.

---

## Step 3 — Seed predefined roles and permissions on startup

Create a new Spring `ApplicationRunner` component that seeds `RbacCatalog.predefinedRoles()`
into the database exactly once (idempotent).

**File:** `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/config/security/RbacInitializer.java`

Implementation rules:
- Inject `RoleRepository` and `PermissionRepository`.
- On startup, iterate `RbacCatalog.predefinedRoles()`.
- For each role: skip if `roleRepository.existsByName(role.getName())` returns `true`.
- For each permission within a role: persist via `permissionRepository.save(permission)`
  only if not already present (`permissionRepository.findByResourceAndAction(resource, action)` returns empty).
- Then save the role with its persisted permissions via `roleRepository.save(role)`.
- Log each role seed action at `INFO` level using SLF4J.
- Annotate the `run` method with `@Transactional` (or let the repository layer handle it).

> **Why here and not in runtime:** This initializer depends on `RoleRepository` and
> `PermissionRepository` beans, which are defined in `AuthConfig` (REST module). Keeping it
> in the same layer avoids layering violations.

---

## Step 4 — Update `UserPrincipal.getAuthorities()` to map real roles

**File:** `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/config/security/UserPrincipal.java`

Current behavior: always returns `Collections.singleton(new SimpleGrantedAuthority("USER"))`.

Required behavior:
- Map each `Role` in `user.getRoles()` to `new SimpleGrantedAuthority("ROLE_" + role.getName())`.
- If the user has no roles assigned, fall back to `ROLE_` + `RbacCatalog.defaultRoleName()`
  (i.e., `ROLE_GUEST`).

Spring Security convention: role names used with `hasRole("ADMIN")` are automatically
prefixed with `ROLE_`; grant authorities must therefore be stored as `ROLE_ADMIN`.

---

## Step 5 — Embed roles in JWT claims

**File:** `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/config/security/JwtService.java`

Changes:
1. Add an overload (or extend the existing method):
   `generateToken(String username, Set<String> roleNames)`.
2. Add a `"roles"` claim containing the set of role name strings.
3. Add a method `extractRoles(String token)` → `List<String>`.
4. Keep the existing `generateToken(String username)` delegate for backwards compatibility
   during migration (calls the new overload with an empty set).

**File:** `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/auth/AuthController.java`

Change the `/login` handler to call `jwtService.generateToken(username, roleNames)` where
`roleNames` is derived from `authenticated.get().getRoles()` mapped to `role.getName()`.

> **Note:** `JwtAuthenticationFilter` loads full authorities from `UserDetails` (live DB),
> so the JWT roles claim is informational / for downstream services. No change is needed in
> the filter itself unless implementing stateless role extraction later.

---

## Step 6 — Update `SecurityConfig` with role-based access control

**File:** `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/config/security/SecurityConfig.java`

Add `@EnableMethodSecurity` annotation to the class (or to `AuthConfig`) to enable
`@PreAuthorize` support for future method-level guards.

Update `filterChain` authorization rules using the following access matrix:

| Path pattern | Methods | Required roles |
|---|---|---|
| `/api/v1/auth/**` | ALL | permit all |
| `/api/v1/users/**` | ALL | `ADMIN` |
| `/api/v1/roles/**` | GET | `ADMIN`, `CLUB_MANAGER`, `ANALYST` |
| `/api/v1/**/` | GET | `ADMIN`, `CLUB_MANAGER`, `ANALYST`, `PRACTITIONER` |
| `/api/v1/**/` | POST, PUT, DELETE | `ADMIN`, `CLUB_MANAGER` |
| Swagger UI / OpenAPI docs | ALL | permit all |
| Actuator | ALL | permit all |

Apply rules using `requestMatchers(...).hasAnyRole(...)` in order of specificity
(most specific first — Spring Security evaluates rules in declaration order).

---

## Step 7 — Implement `UserController` for user management endpoints

Create:
- `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/user/UserController.java`
- `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/user/UserDto.java`
- `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/user/AssignRoleRequest.java`

Endpoints (all under `/api/v1/users`, protected by `ADMIN` role at `SecurityConfig` level):

| Method | Path | Handler method | Notes |
|---|---|---|---|
| `GET` | `/api/v1/users` | `listUsers()` | Returns `List<UserDto>`; delegates to `authenticationService.getUserByUsername(…)` is insufficient — requires `UserRepository.findAll()` injected directly or a new query path |
| `POST` | `/api/v1/users/{id}/assign-role` | `assignRole(UUID id, AssignRoleRequest body)` | Calls `authenticationService.assignRole(id, body.roleName())` |
| `PUT` | `/api/v1/users/{id}/disable` | `disableUser(UUID id)` | Calls `authenticationService.disableUser(id)` |
| `PUT` | `/api/v1/users/{id}/enable` | `enableUser(UUID id)` | Calls `authenticationService.enableUser(id)` |

> **Note on `listUsers`:** `AuthenticationService` does not expose a `findAll` method.
> Inject `UserRepository` directly into `UserController` (or a dedicated read service)
> to fetch all users. This is acceptable since `UserController` is an adapter that can
> depend on domain repository contracts. See architectural note below.

`UserDto` record fields: `UUID id`, `String username`, `String email`, `boolean active`,
`Set<String> roles` (role names), `LocalDateTime createdAt`.

`AssignRoleRequest` record fields: `String roleName`.

`UserController` naming and coding conventions:
- Annotate with `@RestController @RequestMapping(API_BASE_PATH_V1 + "/users")`.
- Use constructor injection for `AuthenticationService` and `UserRepository`.
- Map domain failures (`IllegalArgumentException`, `EntityNotFoundException` or similar)
  to `404 NOT FOUND` at the controller boundary.
- Add OpenAPI `@Operation` and `@ApiResponse` annotations.

---

## Step 8 — Implement `RoleController` for roles catalogue endpoint

Create:
- `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/role/RoleController.java`
- `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/role/RoleDto.java`
- `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/role/PermissionDto.java`

Endpoints:

| Method | Path | Handler | Notes |
|---|---|---|---|
| `GET` | `/api/v1/roles` | `listRoles()` | Returns `List<RoleDto>` via `roleRepository.findAll()` |

`RoleDto` record fields: `UUID id`, `String name`, `List<PermissionDto> permissions`.
`PermissionDto` record fields: `UUID id`, `String resource`, `String action`.

Inject `RoleRepository` via constructor.

---

## Step 9 — Update OpenAPI configuration

**File:** `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/OpenApiConfig.java`

Add a `SecurityScheme` bean for `bearerAuth` (HTTP Bearer / JWT) so that Swagger UI
shows the Authorize button and the generated `openapi.yaml` includes the security scheme:

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(new Components()
            .addSecuritySchemes("bearerAuth",
                new SecurityScheme()
                    .name("bearerAuth")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
}
```

After this step, regenerate and validate:

```bash
python scripts/regenerate_openapi.py
python scripts/verify_openapi.py
swagger-cli validate openapi.yaml
```

---

## Step 10 — Write unit tests

### `UserPrincipalTest`
- Test: user with one role → returns `ROLE_<name>` authority.
- Test: user with multiple roles → returns all as `GrantedAuthority`.
- Test: user with no roles → returns `ROLE_GUEST`.

### `UserControllerTest`
- Mock `AuthenticationService` and `UserRepository`.
- Test `listUsers()` → 200 with mapped DTOs.
- Test `assignRole(id, request)` → 200 on success.
- Test `assignRole(id, unknown-role)` → 404 when service throws.
- Test `disableUser(id)` → 200 on success.

### `RbacInitializerTest`
- Test: when `roleRepository.existsByName()` returns false for all → `saveAll` called once per role.
- Test: when all roles already exist → no seeds performed.

### `JwtServiceTest`
- Test: `generateToken(username, Set.of("ADMIN"))` → decoded token contains `roles` claim.
- Test: `extractRoles(token)` → returns `["ADMIN"]`.

### `AuthControllerTest` (extend existing)
- Test: successful login response includes `token` field; JWT payload contains `roles`.

---

## Step 11 — Compile checkpoint and full test run

```bash
# Compile all
mvn clean compile

# Test REST module
mvn -pl tt-data-league-api-rest test

# Full build with tests
mvn clean install
```

All tests must pass. No new compilation warnings for unchecked casts or deprecated APIs.

---

## Rollback gates

| Gate | Check |
|---|---|
| Compile passes | `mvn clean compile` exits 0 |
| Auth still works | `POST /api/v1/auth/login` returns 200 + JWT |
| JWT contains roles | Decoded payload has `"roles"` array |
| Swagger UI accessible | `GET /swagger-ui/index.html` returns 200 |
| Role seeding idempotent | Two application restarts do not create duplicate roles |
| Protected endpoints enforce roles | `GET /api/v1/users` with `GUEST` JWT → 403 |
| OpenAPI valid | `swagger-cli validate openapi.yaml` exits 0 |

---

## Architectural notes

### Where `findAll` users lives
`AuthenticationService` intentionally does not expose list-all operations.
`UserController` may inject `UserRepository` (domain repository interface) directly.
This is consistent with how `AuthConfig` already wires `userRepository` as a Spring bean:
adapters may consume domain repositories without violating layering rules.

### Transaction boundaries
- `RbacInitializer.run()` should be transactional (seed is a write operation).
- `UserController` handlers that call `authenticationService.assignRole(…)` rely on the
  service layer's own transaction; do not add `@Transactional` to controller methods.

### `@EnableMethodSecurity`
Placing `@EnableMethodSecurity` on `SecurityConfig` enables `@PreAuthorize` on any bean
method in the REST module. Reserve `@PreAuthorize` for fine-grained cross-cutting checks
(e.g., "only modify your own password"). Coarse route-level protection stays in `SecurityConfig.filterChain()`.

### JPA schema impact
`RoleJPA`, `PermissionJPA`, and the `user_roles` join table are defined inside
`tt-data-league-core-repository-jpa`. Because `spring.jpa.hibernate.ddl-auto=update`
is configured in the runtime module, Hibernate will auto-create these tables on first boot.
No manual migration script is required for local development; for production, generate a
Flyway/Liquibase migration before moving `ddl-auto` to `validate`.

---

# Implementation Guidelines

- Apply changes bottom-up: JPA bean wiring → service constructor fix → authority mapping → security rules → controllers → tests.
- Keep all RBAC seeding logic in one place (`RbacInitializer`); do not scatter seed calls across config classes.
- Do not duplicate `Resource`/`PermissionAction`/`RbacCatalog` constants from the external artifact; always import from `tt-data-league-core-domain`.
- Use `RbacCatalog.ADMIN`, `RbacCatalog.CLUB_MANAGER`, etc. as string constants in `SecurityConfig`; do not hardcode role name strings.
- `AuthorizationException` is a domain exception; translate it to `403 FORBIDDEN` at the controller boundary, consistent with other domain exception translations.
- Logging: use `private static final Logger log = LoggerFactory.getLogger(...)` (SLF4J) in all new classes.

---

# Notes

- The `AuthenticationService` constructor breaking change is the single highest-priority fix — nothing will compile or deploy until Step 2 is applied.
- Role seeds (`RbacCatalog.predefinedRoles()`) include a canonical set of `Permission` instances pre-configured per role; the `PermissionRepository` seeding should follow the same idempotency pattern used for roles.
- Future extension point: if a `PRACTITIONER` resource is eventually mapped to the `Practicioner` aggregate, `Resource.PRACTITIONER` is already defined in the catalog.
- Possible follow-up feature: expose a `PATCH /api/v1/users/{id}/password` endpoint for self-service password change (would require authenticated user identity from `SecurityContextHolder`).
- The `ANALYST` role is read-only by catalog design; ensure `SecurityConfig` write-method guards exclude `ANALYST` in `hasAnyRole(...)` lists.

