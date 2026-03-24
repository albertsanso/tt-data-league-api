# tt-data-league-api-graphql Module - Agentic Development Guide

## Module Purpose

**GraphQL API Layer** - Exposes application functionality through GraphQL queries and mutations. Provides an alternative to REST API with flexible query capabilities and strong typing.

## Module Role in Architecture

```
GraphQL Clients (Apollo Client, Relay, etc.)
        ↓
  [GRAPHQL RESOLVERS] ← You are here
        ↓
   Core Module (Commands/Queries)
        ↓
    Repositories
        ↓
   Domain Model
```

This module translates GraphQL queries/mutations into domain Commands/Queries and dispatches them through the CommandBus/QueryBus.

## Module Dependencies

### Direct Dependencies
- `tt-data-league-core-domain` - Domain models
- `tt-data-league-core-repository-jpa` - Repository contracts
- `tt-data-league-api-core` - Commands, Queries, Handlers
- `spring-graphql` - Spring GraphQL support
- `graphql-java` (v21.0) - GraphQL Java implementation
- `spring-boot-starter-web` - Web support
- `querybus-synchronous-inmemory` - Query bus
- `commons-core` - Utility interfaces
- Lombok - Code generation

### Inverse Dependencies
- `tt-data-league-api-runtime` - Aggregates this module

## Directory Structure

```
src/main/java/org/cttelsamicsterrassa/data/api/graphql/
├── config/
│   └── GraphQLConfig.java           # GraphQL configuration
├── club/
│   ├── ClubResolver.java            # Query/Mutation resolvers
│   └── ClubDataLoader.java          # Batch loading optimization
├── club_member/                     # Similar structure
├── match/
├── practicioner/
└── season_player/

resources/
├── graphql/
│   └── schema.graphqls              # GraphQL schema definitions
└── application.properties            # GraphQL configuration
```

## GraphQL Schema Structure

### Example Schema Pattern

```graphql
# Define types
type Club {
  id: ID!
  name: String!
  yearRanges: [String!]!
  members: [ClubMember!]!
}

type ClubMember {
  id: ID!
  name: String!
  club: Club!
}

# Input types for mutations
input CreateClubInput {
  name: String!
  yearRanges: [String!]!
}

input ModifyClubInput {
  id: ID!
  name: String!
  yearRanges: [String!]!
}

# Queries
type Query {
  club(id: ID!): Club
  clubs: [Club!]!
  clubByName(name: String!): Club
  clubsBySimilarName(name: String!): [Club!]!
}

# Mutations
type Mutation {
  createClub(input: CreateClubInput!): Club!
  modifyClub(input: ModifyClubInput!): Club!
  deleteClub(id: ID!): Boolean!
}
```

## Key Components

### 1. Resolvers

**Purpose:** Handle GraphQL queries and mutations by dispatching Commands/Queries

**Types:**
- **Query Resolvers**: Handle top-level `query { }` operations
- **Mutation Resolvers**: Handle top-level `mutation { }` operations
- **Field Resolvers**: Handle nested field resolution

### 2. Data Loaders

**Purpose:** Optimize N+1 query problems by batching database calls

### 3. GraphQL Configuration

**Purpose:** Configure GraphQL executor, error handling, instrumentation

## Code Patterns

### Creating a Query Resolver

```java
package org.cttelsamicsterrassa.data.api.graphql.{domain};

import org.albertsanso.commons.query.QueryBus;
import org.cttelsamicsterrassa.data.api.core.{domain}.find.application.*;
import org.cttelsamicsterrassa.data.core.domain.model.{Entity};
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;
import java.util.List;

@Controller
public class {Entity}Resolver {

    private final QueryBus queryBus;

    public {Entity}Resolver(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    @QueryMapping
    public {Entity} {entity}(
        @Argument UUID id
    ) {
        Find{Entity}ByIdQuery query = new Find{Entity}ByIdQuery(id);
        var response = queryBus.ask(query);
        return ({Entity}) response.getResult();
    }

    @QueryMapping
    public List<{Entity}> {entities}() {
        FindAll{Entity}sQuery query = new FindAll{Entity}sQuery();
        var response = queryBus.ask(query);
        return (List<{Entity}>) response.getResult();
    }

    @QueryMapping
    public {Entity} {entity}ByName(
        @Argument String name
    ) {
        Find{Entity}ByNameQuery query = new Find{Entity}ByNameQuery(name);
        var response = queryBus.ask(query);
        return ({Entity}) response.getResult();
    }
}
```

### Creating a Mutation Resolver

```java
package org.cttelsamicsterrassa.data.api.graphql.{domain};

import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.command.DomainCommandResponse;
import org.cttelsamicsterrassa.data.api.core.{domain}.create.application.Create{Entity}Command;
import org.cttelsamicsterrassa.data.api.core.{domain}.modify.application.Modify{Entity}Command;
import org.cttelsamicsterrassa.data.api.core.{domain}.delete.application.Delete{Entity}Command;
import org.cttelsamicsterrassa.data.core.domain.model.{Entity};
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class {Entity}MutationResolver {

    private final CommandBus commandBus;

    public {Entity}MutationResolver(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @MutationMapping
    public {Entity} create{Entity}(
        @Argument String name,
        @Argument List<String> yearRanges
    ) {
        Create{Entity}Command cmd = new Create{Entity}Command(
            UUID.randomUUID(),
            name,
            yearRanges
        );
        
        DomainCommandResponse response = commandBus.execute(cmd);
        
        if (response.isSuccess()) {
            return ({Entity}) response.getResult();
        }
        
        throw new RuntimeException("Failed to create {entity}: " + response.getError());
    }

    @MutationMapping
    public {Entity} modify{Entity}(
        @Argument UUID id,
        @Argument String name,
        @Argument List<String> yearRanges
    ) {
        Modify{Entity}Command cmd = new Modify{Entity}Command(
            id,
            name,
            yearRanges
        );
        
        DomainCommandResponse response = commandBus.execute(cmd);
        
        if (response.isSuccess()) {
            return ({Entity}) response.getResult();
        }
        
        throw new RuntimeException("Failed to modify {entity}: " + response.getError());
    }

    @MutationMapping
    public Boolean delete{Entity}(
        @Argument UUID id
    ) {
        Delete{Entity}Command cmd = new Delete{Entity}Command(id);
        DomainCommandResponse response = commandBus.execute(cmd);
        return response.isSuccess();
    }
}
```

### GraphQL Schema Definition

```graphql
# Define the Club type matching domain model
type Club {
  id: ID!
  name: String!
  yearRanges: [String!]!
  members: [ClubMember!]!
  createdAt: String!
}

type ClubMember {
  id: ID!
  name: String!
  joinDate: String!
  club: Club!
}

# Input types for mutations
input CreateClubInput {
  name: String!
  yearRanges: [String!]!
}

input ModifyClubInput {
  id: ID!
  name: String!
  yearRanges: [String!]!
}

# Query operations
type Query {
  # Single club queries
  club(id: ID!): Club
  clubByName(name: String!): Club
  clubsBySimilarName(name: String!): [Club!]!
  
  # List queries
  clubs: [Club!]!
  clubMembers(clubId: ID!): [ClubMember!]!
}

# Mutation operations
type Mutation {
  createClub(input: CreateClubInput!): Club!
  modifyClub(input: ModifyClubInput!): Club!
  deleteClub(id: ID!): Boolean!
  
  addMemberToClub(clubId: ID!, memberId: ID!): ClubMember!
  removeMemberFromClub(clubId: ID!, memberId: ID!): Boolean!
}
```

### GraphQL Configuration

```java
package org.cttelsamicsterrassa.data.api.graphql.config;

import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.SimpleDataFetcherExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.GraphQlSource;

@Configuration
public class GraphQLConfig {

    @Bean
    public DataFetcherExceptionHandler dataFetcherExceptionHandler() {
        return new SimpleDataFetcherExceptionHandler();
    }
    
    // Additional GraphQL beans and configuration
}
```

## Data Loaders for Performance

### Example Data Loader

```java
package org.cttelsamicsterrassa.data.api.graphql.{domain};

import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.stereotype.Component;
import org.cttelsamicsterrassa.data.core.repository.jpa.{Entity}Repository;

import java.util.*;

@Component
public class {Entity}DataLoader {

    private final {Entity}Repository repository;

    public {Entity}DataLoader(
        {Entity}Repository repository,
        BatchLoaderRegistry registry
    ) {
        this.repository = repository;
        
        // Register batch loader for finding multiple {entities}
        registry.forTypePair(UUID.class, {Entity}.class)
            .registerBatchLoader((ids, env) -> 
                repository.findAllById(ids)
                    .stream()
                    .collect(HashMap::new, 
                        (m, e) -> m.put(e.getId(), e),
                        Map::putAll)
            );
    }
}
```

## Important Guidelines

### DO:
✅ Inject CommandBus and QueryBus via constructor  
✅ Use @QueryMapping for queries  
✅ Use @MutationMapping for mutations  
✅ Use @Argument for parameters  
✅ Throw GraphQL exceptions for errors  
✅ Define complete GraphQL schema  
✅ Use data loaders for batch operations  
✅ Keep resolvers thin - delegate to bus  

### DON'T:
❌ Expose domain model directly (use GraphQL types)  
❌ Put business logic in resolvers  
❌ Mix multiple entities in one resolver  
❌ Create circular dependencies in schema  
❌ Forget input validation  
❌ Return raw exceptions  
❌ Hardcode configuration values  
❌ Create N+1 query problems without data loaders

## Testing Patterns

Test resolvers with:
1. GraphQLTester for GraphQL operations
2. Mock CommandBus/QueryBus
3. Assert response data
4. Assert error handling

## Application Properties

Key GraphQL configurations in `application.properties`:
```properties
# GraphQL
spring.graphql.graphiql.enabled=true
spring.graphql.path=/graphql
spring.graphql.schema.locations=classpath:graphql/

# CORS for GraphQL endpoint
spring.web.cors.allowed-origins=*
```

## Schema File Location

GraphQL schema files are located in:
```
src/main/resources/graphql/schema.graphqls
```

The schema is the contract between client and server, defining all available:
- Query operations
- Mutations
- Types
- Input types
- Subscriptions (if supported)

## Common Queries

### Query Examples
```graphql
query GetClub {
  club(id: "123e4567-e89b-12d3-a456-426614174000") {
    id
    name
    yearRanges
  }
}

query GetAllClubs {
  clubs {
    id
    name
  }
}
```

### Mutation Examples
```graphql
mutation CreateNewClub {
  createClub(input: {
    name: "New Club",
    yearRanges: ["2023-2024"]
  }) {
    id
    name
  }
}

mutation UpdateClub {
  modifyClub(input: {
    id: "123e4567-e89b-12d3-a456-426614174000",
    name: "Updated Club",
    yearRanges: ["2023-2024", "2024-2025"]
  }) {
    id
    name
  }
}
```

## Related Modules

- **Core Layer** ([tt-data-league-api-core](../tt-data-league-api-core/AGENT.md)): Handles command/query execution
- **REST Layer** ([tt-data-league-api-rest](../tt-data-league-api-rest/AGENT.md)): Alternative REST API interface
- **Repository Layer** ([tt-data-league-api-repository-jpa](../tt-data-league-api-repository-jpa/AGENT.md)): Persistence operations
- **Runtime** ([tt-data-league-api-runtime](../tt-data-league-api-runtime/AGENT.md)): Application bootstrapping

