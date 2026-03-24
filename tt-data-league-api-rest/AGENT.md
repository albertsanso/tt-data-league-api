# tt-data-league-api-rest Module - Agentic Development Guide

## Module Purpose

**REST API Layer** - Exposes application functionality through RESTful HTTP endpoints. Handles HTTP request/response serialization, validation, error handling, and authentication/authorization.

## Module Role in Architecture

```
HTTP Clients (Postman, frontend, etc.)
        ↓
  [REST CONTROLLERS] ← You are here
        ↓
   Core Module (Commands/Queries)
        ↓
    Repositories
        ↓
   Domain Model
```

This module translates HTTP requests into Commands/Queries and dispatches them through the CommandBus/QueryBus.

## Module Dependencies

### Direct Dependencies
- `tt-data-league-core-domain` - Domain models
- `tt-data-league-api-core` - Commands, Queries, Handlers
- `spring-boot-starter-web` - Spring Web MVC
- `spring-boot-starter-security` - Security framework
- `spring-boot-starter-validation` - Bean validation (@Valid)
- `spring-security-core` - Security utilities
- `jackson-databind` - JSON serialization
- JJWT (JWT tokens) - JWT authentication
- Lombok - Code generation

### Inverse Dependencies
- `tt-data-league-api-runtime` - Aggregates this module

## Directory Structure

```
src/main/java/org/cttelsamicsterrassa/data/api/rest/
├── club/
│   ├── ClubController.java          # REST endpoints
│   ├── ClubDto.java                 # Request/Response DTO
│   └── ClubOpenAPIv1Controller.java # OpenAPI annotations
├── club_member/                     # Similar structure
├── match/
├── practicioner/
├── season_player/
├── season_player_result/
├── error/
│   └── ErrorHandler.java            # Global exception handler
└── shared/
    ├── JwtTokenProvider.java        # JWT utilities
    ├── JwtAuthenticationFilter.java  # JWT request filter
    └── SecurityConfig.java          # Spring Security configuration

resources/
└── application.properties            # REST configuration
```

## Key Components

### 1. REST Controllers

**Purpose:** Handle HTTP requests and delegate to CommandBus/QueryBus

**Example Controller Pattern:**

```
GET    /club/find_by_id?id={id}         → FindClubByIdQuery
GET    /club/find_all                   → FindAllClubsQuery
GET    /club/find_by_name?name={name}   → FindClubByNameQuery
GET    /club/find_similar?name={name}   → FindClubBySimilarNameQuery
POST   /club/create                     → CreateClubCommand
PUT    /club/modify                     → ModifyClubCommand
DELETE /club/delete/{id}                → DeleteClubCommand
```

**Controller Naming:** `{Entity}Controller` (e.g., `ClubController`)

**OpenAPI Annotations:** `{Entity}OpenAPIv1Controller` class defines @RequestMapping with version and tags

### 2. Data Transfer Objects (DTOs)

**Purpose:** Define request/response contracts for REST endpoints

**Properties:**
- Match REST endpoint requirements
- Include validation annotations (@NotNull, @Email, etc.)
- Simple POJO structure with getters/setters

### 3. Error Handling

**GlobalExceptionHandler:** Catches exceptions and returns standardized error responses

**Error Response Format:**
```json
{
  "status": 400,
  "message": "Bad Request",
  "details": "Validation failed",
  "timestamp": "2026-03-23T10:00:00Z"
}
```

### 4. Security Configuration

**Components:**
- `SecurityConfig.java` - Spring Security bean configuration
- `JwtTokenProvider.java` - JWT token creation/validation
- `JwtAuthenticationFilter.java` - Extracts JWT from headers
- Protected endpoints via @PreAuthorize or SecurityConfig

## Code Patterns

### Creating a REST Controller

```java
package org.cttelsamicsterrassa.data.api.rest.{domain};

import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.albertsanso.commons.query.QueryBus;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.cttelsamicsterrassa.data.api.core.{domain}.create.application.Create{Entity}Command;
import org.cttelsamicsterrassa.data.api.core.{domain}.find.application.Find{Entity}ByIdQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@RestController
@{Entity}OpenAPIv1Controller
@Tag(name = "{Entity}", description = "{Entity} API")
public class {Entity}Controller {

    @Autowired
    private QueryBus queryBus;

    @Autowired
    private CommandBus commandBus;

    @GetMapping("/find_by_id")
    @Operation(summary = "Find {entity} by ID")
    public ResponseEntity<?> findById(@RequestParam UUID id) {
        Find{Entity}ByIdQuery query = new Find{Entity}ByIdQuery(id);
        DomainQueryResponse response = queryBus.ask(query);
        
        if (response.getResult() == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(response.getResult());
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new {entity}")
    public ResponseEntity<?> create(@RequestBody Create{Entity}Dto dto) {
        Create{Entity}Command cmd = new Create{Entity}Command(
            UUID.randomUUID(),
            dto.getName(),
            dto.getOtherFields()
        );
        
        DomainCommandResponse response = commandBus.execute(cmd);
        
        if (response.isSuccess()) {
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response.getResult());
        }
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response.getError());
    }
}
```

### Creating a DTO

```java
package org.cttelsamicsterrassa.data.api.rest.{domain};

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class {Entity}Dto {
    
    @NotNull(message = "Name is required")
    private String name;
    
    private String description;
    
    // Getters and setters (or use Lombok @Getter @Setter)
}
```

### Creating an OpenAPI v1 Controller Annotation

```java
package org.cttelsamicsterrassa.data.api.rest.{domain};

import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/v1/{domain}")
@Tag(name = "{Entity}", description = "{Entity} Management API")
public @interface {Entity}OpenAPIv1Controller {
}
```

### Global Exception Handler

```java
package org.cttelsamicsterrassa.data.api.rest.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("message", "Validation failed");
        errors.put("timestamp", LocalDateTime.now());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        errors.put("errors", fieldErrors);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralExceptions(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("message", "Internal Server Error");
        error.put("details", ex.getMessage());
        error.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

## REST Endpoint Patterns

### Standard CRUD Endpoints

```
GET    /api/v1/{domain}/find_all                    → List all entities
GET    /api/v1/{domain}/find_by_id?id={id}         → Get by ID
GET    /api/v1/{domain}/find_by_name?name={name}   → Custom search
POST   /api/v1/{domain}/create                      → Create new entity
PUT    /api/v1/{domain}/modify                      → Update entity
DELETE /api/v1/{domain}/delete/{id}                 → Delete entity
```

### Status Codes
- `200 OK` - Successful read operation
- `201 Created` - Successful creation
- `204 No Content` - Successful deletion
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Authorization failed
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## Important Guidelines

### DO:
✅ Use @Autowired to inject CommandBus and QueryBus  
✅ Create DTOs for request/response contracts  
✅ Use @Valid for input validation  
✅ Return proper HTTP status codes  
✅ Add OpenAPI/Swagger annotations  
✅ Handle exceptions globally  
✅ Use ResponseEntity for flexible responses  
✅ Keep controllers thin - delegate to bus  

### DON'T:
❌ Expose domain model directly (use DTOs)  
❌ Put business logic in controllers  
❌ Forget HTTP method annotations (@GetMapping, @PostMapping, etc.)  
❌ Mix different API versions in same controller  
❌ Skip input validation  
❌ Return raw exception messages  
❌ Hardcode configuration values  

## Testing Patterns

Test controllers with:
1. MockMvc for HTTP testing
2. Mock CommandBus/QueryBus
3. Assert response status codes
4. Assert response body content

## Application Properties

Key configurations in `application.properties`:
```properties
# Server
server.port=8080
server.servlet.context-path=/

# Security
jwt.secret=your-secret-key
jwt.expiration=3600000

# Logging
logging.level.root=INFO
logging.level.org.cttelsamicsterrassa=DEBUG
```

## Security Features

- **JWT Authentication**: Stateless token-based authentication
- **Request Filtering**: JwtAuthenticationFilter validates tokens
- **Authorization**: @PreAuthorize for method-level security
- **CORS**: Configurable in SecurityConfig

## Related Modules

- **Core Layer** ([tt-data-league-api-core](../tt-data-league-api-core/AGENT.md)): Handles command/query execution
- **GraphQL Layer** ([tt-data-league-api-graphql](../tt-data-league-api-graphql/AGENT.md)): Alternative API interface
- **Repository Layer** ([tt-data-league-api-repository-jpa](../tt-data-league-api-repository-jpa/AGENT.md)): Persistence operations
- **Runtime** ([tt-data-league-api-runtime](../tt-data-league-api-runtime/AGENT.md)): Application bootstrapping

