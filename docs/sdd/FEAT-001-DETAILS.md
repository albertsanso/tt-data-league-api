# Build Plan

## Context — current state
The previous implementation session partially moved auth/security classes from `runtime` into `rest`, but cloned the local `Users`/`UserRepo`/`UserService` entity stack instead of using core-domain.
The plan below describes the correct target state and the exact steps to reach it from the current mixed state.

## Core-domain auth API available (tt-data-league-core-domain)
| Class | Package | Role |
|---|---|---|
| `User` | `model.auth` | Domain entity: `createNew(username,email,password)`, `verifyPassword(raw)`, `getPasswordHash()` |
| `UserRepository` | `repository.auth` | Interface: `findByUsername`, `save`, `existsByUsername`, etc. |
| `AuthenticationService` | `service.auth` | `registerUser(username,email,password)`, `authenticateUser(username,password)` → `Optional<User>` |
| `BcryptPasswordHasher` | `service.auth` | Implements `PasswordHasher` |
| `UserValidator` | `service.auth` | Validates username/email/password fields |
| `InvalidCredentialsException` | `service.auth` | Thrown on bad login |
| `UserAlreadyExistsException` | `service.auth` | Thrown on duplicate register |

## Core-repository-jpa JPA wiring (tt-data-league-core-repository-jpa)
| Class | Role |
|---|---|
| `UserRepositoryImpl` | Implements `UserRepository`; constructor: `(UserRepositoryHelper, UserJPAToUserMapper, UserToUserJPAMapper)` |
| `UserRepositoryHelper` | Spring Data `JpaRepository<UserJPA, UUID>`; auto-created by Spring (already covered by `@EnableJpaRepositories("org.cttelsamicsterrassa")` in `APIApplication`) |
| `UserJPA` | JPA entity (already covered by `@EntityScan("org.cttelsamicsterrassa")` in `APIApplication`) |
| `UserJPAToUserMapper` / `UserToUserJPAMapper` | Plain instantiable mappers (no-arg constructors) |

---

## Step 1 — Update REST pom.xml
Add `tt-data-league-core-repository-jpa` dependency (needed for `UserRepositoryImpl`, `UserRepositoryHelper`, and mappers). The `spring-boot-starter-data-jpa` is already present from the previous session.

```xml
<!-- add to tt-data-league-api-rest/pom.xml -->
<dependency>
    <groupId>org.cttelsamicsterrassa</groupId>
    <artifactId>tt-data-league-core-repository-jpa</artifactId>
</dependency>
```

## Step 2 — Delete wrong local classes from REST config/security
Delete from `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/config/security/`:
- `Users.java` — replaced by core-domain `User`
- `UserRepo.java` — replaced by core-domain `UserRepository`
- `UserService.java` — replaced by core-domain `AuthenticationService`

Keep: `JwtService.java`, `JwtAuthenticationFilter.java`, `SecurityConfig.java`, `TokenBlacklistService.java`, `LoginResponse.java`
Adapt: `UserPrincipal.java`, `MyUserDetailsService.java` (see Steps 4–5)

## Step 3 — Create REST auth DTOs
Create in `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/auth/`:

- **`RegisterRequest.java`** — `{ String username; String email; String password; }` (email required by core-domain `User.createNew`)
- **`LoginRequest.java`** — `{ String username; String password; }` (replaces `Users` as login request body)

## Step 4 — Adapt UserPrincipal.java
Change field from local `Users` to core-domain `User` (`org.cttelsamicsterrassa.data.core.domain.model.auth.User`).
Update `getPassword()` to return `user.getPasswordHash()` instead of `user.getPassword()`.

## Step 5 — Adapt MyUserDetailsService.java
Replace injected `UserRepo` (local) with `UserRepository` (core-domain interface). Change `loadUserByUsername` to use `userRepository.findByUsername(username)` which returns `Optional<User>`.

## Step 6 — Create AuthConfig.java (Spring bean wiring)
Create `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/config/security/AuthConfig.java`:

```java
@Configuration
public class AuthConfig {
    @Bean UserValidator userValidator() { return new UserValidator(); }
    @Bean PasswordHasher passwordHasher() { return new BcryptPasswordHasher(); }
    @Bean UserToUserJPAMapper userToUserJPAMapper() { return new UserToUserJPAMapper(); }
    @Bean UserJPAToUserMapper userJPAToUserMapper() { return new UserJPAToUserMapper(); }
    @Bean UserRepository userRepository(UserRepositoryHelper helper,
                                        UserJPAToUserMapper j2u,
                                        UserToUserJPAMapper u2j) {
        return new UserRepositoryImpl(helper, j2u, u2j);
    }
    @Bean AuthenticationService authenticationService(UserRepository repo,
                                                       PasswordHasher hasher,
                                                       UserValidator validator) {
        return new AuthenticationService(repo, hasher, validator);
    }
}
```

## Step 7 — Adapt AuthController.java in REST
File: `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/auth/AuthController.java`

- Inject `AuthenticationService` (replaces local `UserService`) and `TokenBlacklistService` + `JwtService` (for logout expiry extraction).
- `/register` → takes `RegisterRequest`, calls `authService.registerUser(req.getUsername(), req.getEmail(), req.getPassword())`, returns 201 with registered user's username.
- `/login` → takes `LoginRequest`, calls `authService.authenticateUser(req.getUsername(), req.getPassword())`, returns `LoginResponse(token, username)` on success, 401 on empty Optional.
- `/logout` → extract token from `Authorization: Bearer ...` header, call `blacklistService.blacklistToken(token, jwtService.extractExpiration(token))`, return 200.
- Handle exceptions: `InvalidCredentialsException` → 401, `UserAlreadyExistsException` → 409, `ValidationException` → 400.

## Step 8 — Delete all original runtime auth/security classes
Delete from `tt-data-league-api-runtime/src/main/java/`:
- `org/cttelsamicsterrassa/data/api/rest/auth/AuthController.java`
- `org/cttelsamicsterrassa/data/api/runtime/config/security/Users.java`
- `org/cttelsamicsterrassa/data/api/runtime/config/security/UserService.java`
- `org/cttelsamicsterrassa/data/api/runtime/config/security/UserRepo.java`
- `org/cttelsamicsterrassa/data/api/runtime/config/security/UserPrincipal.java`
- `org/cttelsamicsterrassa/data/api/runtime/config/security/LoginResponse.java`
- `org/cttelsamicsterrassa/data/api/runtime/config/security/SecurityConfig.java`
- `org/cttelsamicsterrassa/data/api/runtime/config/security/JwtAuthenticationFilter.java`
- `org/cttelsamicsterrassa/data/api/runtime/config/security/JwtService.java`
- `org/cttelsamicsterrassa/data/api/runtime/config/security/TokenBlacklistService.java`
- `org/cttelsamicsterrassa/data/api/runtime/config/security/MyUserDetailsService.java`

Keep: `APIApplication.java`, `AppConfig.java`

## Step 9 — Clean runtime pom.xml
Remove `spring-security-crypto` and `spring-boot-starter-security` from runtime `pom.xml` — security is now owned by the REST module (transitively available via `tt-data-league-api-rest` dependency).

## Step 10 — Verify no stale references
Confirm zero results for `runtime.config.security` across all Java source files in the repository.

## Step 11 — Compile checkpoint
```bash
mvn -pl tt-data-league-api-rest -am clean compile
mvn -pl tt-data-league-api-runtime -am -DskipTests compile
```
Fix any compile errors before proceeding.

## Step 12 — Update auth tests in REST
Adapt `AuthControllerTest.java`: mock `AuthenticationService`, use `RegisterRequest`/`LoginRequest` DTOs. `TokenBlacklistServiceTest.java` is unchanged.

## Step 13 — Run full test suite
```bash
mvn -pl tt-data-league-api-rest -am test
```
All tests (auth, blacklist, OpenAPI) must pass before marking done.

---

## Rollback gates
| Checkpoint | Pass condition | Rollback action |
|---|---|---|
| After Steps 2–3 (delete/add files) | REST module compiles | Restore deleted files from git |
| After Step 8 (delete runtime files) | Runtime compiles | `git checkout` deleted runtime files |
| After Step 13 (tests) | All tests pass | Revert latest changeset and re-run |

# Notes
- Dont use Users, UserService, UserRepo in the rest module. Instead use the entities and services provided by the dependency tt-data-league-core-domain.
- Move also SecurityConfig and related JWT utilities to the rest module to keep all security-related code together.