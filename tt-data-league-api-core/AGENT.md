# tt-data-league-api-core Module - Agentic Development Guide

## Module Purpose

**Core Application Logic Layer** - Contains all Commands, Queries, and their Handlers that implement the business logic using the CQRS pattern. This module is framework-independent and focuses purely on application orchestration.

## Module Role in Architecture

```
REST/GraphQL Controllers
        ↓
  [CORE MODULE] ← You are here
        ↓
    Repositories
        ↓
   Domain Model
```

This module acts as the heart of the application, orchestrating:
- Command execution (mutations)
- Query execution (reads)
- Cross-cutting concerns

## Module Dependencies

### Direct Dependencies
- `tt-data-league-core-domain` (external) - Domain models and entities
- `tt-data-league-core-repository-jpa` (external) - Repository contracts
- `commandbus-synchronous-inmemory` - Command bus for dispatching commands
- `querybus-synchronous-inmemory` - Query bus for executing queries
- `eventbus-synchronous-inmemory` - Event bus for domain events
- `commons-core` - Utility interfaces (DomainCommand, DomainCommand, DomainQueryResponse)
- Spring Boot starter

### Inverse Dependencies
- `tt-data-league-api-rest` - REST controllers dispatch commands/queries
- `tt-data-league-api-graphql` - GraphQL resolvers dispatch commands/queries
- `tt-data-league-api-runtime` - Aggregates all modules

## Directory Structure

```
src/main/java/org/cttelsamicsterrassa/data/api/core/
├── club/
│   ├── create/
│   │   └── application/
│   │       ├── CreateClubCommand.java
│   │       └── CreateClubCommandHandler.java
│   ├── delete/
│   │   └── application/
│   │       ├── DeleteClubCommand.java
│   │       └── DeleteClubCommandHandler.java
│   ├── find/
│   │   └── application/
│   │       ├── FindAllClubsQuery.java
│   │       ├── FindAllClubsQueryHandler.java
│   │       ├── FindClubByIdQuery.java
│   │       ├── FindClubByIdQueryHandler.java
│   │       ├── FindClubByNameQuery.java
│   │       ├── FindClubByNameQueryHandler.java
│   │       ├── FindClubBySimilarNameQuery.java
│   │       └── FindClubBySimilarNameQueryHandler.java
│   └── modify/
│       └── application/
│           ├── ModifyClubCommand.java
│           └── ModifyClubCommandHandler.java
│
├── club_member/      # Similar structure for club members
├── match/            # Similar structure for matches
├── practicioner/     # Similar structure for practitioners
├── season_player/    # Similar structure for season players
└── season_player_result/  # Similar structure for season player results
```

## Domain Modules

### 1. Club Management
**Path:** `club/`

**Commands:**
- `CreateClubCommand(UUID clubId, String name, List<String> yearRanges)`
- `ModifyClubCommand(UUID clubId, String name, List<String> yearRanges)`
- `DeleteClubCommand(UUID clubId)`

**Queries:**
- `FindAllClubsQuery()` → List<Club>
- `FindClubByIdQuery(UUID clubId)` → Club
- `FindClubByNameQuery(String name)` → Club
- `FindClubBySimilarNameQuery(String name)` → List<Club>

**Handler Responsibilities:**
- Validate input parameters
- Call repository operations
- Handle domain events
- Return appropriate responses

### 2. Club Member Management
**Path:** `club_member/`

Similar structure to Club with CRUD operations for club membership.

### 3. Match Management
**Path:** `match/`

Commands and Queries for managing league matches.

### 4. Practitioner Management
**Path:** `practicioner/`

Commands and Queries for managing league players (practitioners).

### 5. Season Player Management
**Path:** `season_player/`

Commands and Queries for managing player participation in seasons.

### 6. Season Player Result Management
**Path:** `season_player_result/`

Commands and Queries for managing match results.

## Code Patterns

### Creating a New Command

```java
package org.cttelsamicsterrassa.data.api.core.{domain}.{operation}.application;

import org.albertsanso.commons.command.DomainCommand;
import java.time.ZonedDateTime;
import java.util.UUID;

public class Create{Entity}Command extends DomainCommand {
    
    private final UUID id;
    private final String name;
    // ... other fields
    
    public Create{Entity}Command(UUID id, String name, ...) {
        super(ZonedDateTime.now(), UUID.randomUUID().toString());
        this.id = id;
        this.name = name;
        // ... assign other fields
    }
    
    // Getters for all fields
    public UUID getId() { return id; }
    public String getName() { return name; }
}
```

### Creating a Command Handler

```java
package org.cttelsamicsterrassa.data.api.core.{domain}.{operation}.application;

import org.albertsanso.commons.command.CommandHandler;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.springframework.stereotype.Component;
import org.cttelsamicsterrassa.data.core.repository.jpa.{Entity}Repository;

@Component
public class Create{Entity}CommandHandler implements CommandHandler<Create{Entity}Command> {
    
    private final {Entity}Repository repository;
    
    public Create{Entity}CommandHandler({Entity}Repository repository) {
        this.repository = repository;
    }
    
    @Override
    public DomainCommandResponse handle(Create{Entity}Command command) {
        // 1. Validate
        // 2. Create entity
        // 3. Save via repository
        // 4. Publish events if needed
        // 5. Return response
        
        {Entity} entity = new {Entity}(command.getId(), command.getName());
        repository.save(entity);
        return new DomainCommandResponse(true, entity);
    }
}
```

### Creating a Query

```java
package org.cttelsamicsterrassa.data.api.core.{domain}.{operation}.application;

import org.albertsanso.commons.query.DomainQuery;
import java.util.UUID;

public class Find{Entity}ByIdQuery extends DomainQuery {
    
    private final UUID id;
    
    public Find{Entity}ByIdQuery(UUID id) {
        super(UUID.randomUUID().toString());
        this.id = id;
    }
    
    public UUID getId() { return id; }
}
```

### Creating a Query Handler

```java
package org.cttelsamicsterrassa.data.api.core.{domain}.{operation}.application;

import org.albertsanso.commons.query.QueryHandler;
import org.albertsanso.commons.query.DomainQueryResponse;
import org.springframework.stereotype.Component;
import org.cttelsamicsterrassa.data.core.repository.jpa.{Entity}Repository;
import org.cttelsamicsterrassa.data.core.domain.model.{Entity};

@Component
public class Find{Entity}ByIdQueryHandler implements QueryHandler<Find{Entity}ByIdQuery> {
    
    private final {Entity}Repository repository;
    
    public Find{Entity}ByIdQueryHandler({Entity}Repository repository) {
        this.repository = repository;
    }
    
    @Override
    public DomainQueryResponse handle(Find{Entity}ByIdQuery query) {
        {Entity} entity = repository.findById(query.getId()).orElse(null);
        return new DomainQueryResponse(entity);
    }
}
```

## Important Guidelines

### DO:
✅ Inject repositories in handlers (constructor injection)  
✅ Return `DomainCommandResponse` or `DomainQueryResponse`  
✅ Validate inputs before processing  
✅ Keep business logic in handlers  
✅ Use descriptive command/query class names  
✅ Handle exceptions gracefully  
✅ Follow the application structure (domain/operation/application pattern)

### DON'T:
❌ Directly inject external services in REST/GraphQL layers  
❌ Mix query and command logic  
❌ Return domain models from queries (use DTOs in API layers)  
❌ Create circular dependencies between modules  
❌ Put HTTP/GraphQL concerns in core module  
❌ Ignore the bus pattern - always dispatch via CommandBus/QueryBus

## Testing Patterns

Test handlers directly by:
1. Mocking repositories
2. Creating command/query objects
3. Invoking handler.handle()
4. Asserting DomainCommandResponse or DomainQueryResponse

## Common Modifications

### Add a new CRUD operation for an entity:
1. Create Command class in `{domain}/{operation}/application/`
2. Create CommandHandler class
3. Add @Component annotation
4. Implement CommandHandler interface
5. Inject required repositories
6. Repeat for Query if it's a read operation

### Extend an existing handler:
1. Open the handler class
2. Modify the handle() method logic
3. Update related Command/Query if needed
4. Update tests

## Related Modules

- **REST Layer** ([tt-data-league-api-rest](../tt-data-league-api-rest/AGENT.md)): Calls handlers via CommandBus/QueryBus
- **GraphQL Layer** ([tt-data-league-api-graphql](../tt-data-league-api-graphql/AGENT.md)): Similar pattern for GraphQL
- **Repository Layer** ([tt-data-league-api-repository-jpa](../tt-data-league-api-repository-jpa/AGENT.md)): Implements repositories used by handlers

