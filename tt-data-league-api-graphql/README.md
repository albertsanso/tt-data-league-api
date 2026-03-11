# tt-data-league-api-graphql

GraphQL API implementation for the Table Tennis League data management system.

## Overview

This module provides a GraphQL layer for the tt-data-league-api project, allowing clients to query domain entities using GraphQL instead of REST.

## Architecture

### GraphQL Schema (`src/main/resources/graphql/schema.graphqls`)
- Defines GraphQL types for: Club, Practicioner, ClubMember, SeasonPlayer, Match
- Includes Query type with operations to find and list entities
- Supports nested queries for relationships between entities

### Data Transfer Objects (DTOs)
Located in `src/main/java/org/cttelsamicsterrassa/data/api/graphql/`

- `club/ClubGraphQLDto.java` - Club data
- `practicioner/PracticionerGraphQLDto.java` - Practicioner (player) data
- `club_member/ClubMemberGraphQLDto.java` - Club membership data
- `season_player/SeasonPlayerGraphQLDto.java` - Season player data with license info
- `match/MatchGraphQLDto.java` - Match data

All DTOs include `fromDomain()` methods to convert from domain entities.

### Resolvers
Located in `src/main/java/org/cttelsamicsterrassa/data/api/graphql/`

GraphQL resolvers are implemented using Spring's `@QueryMapping` and `@SchemaMapping` annotations:

1. **ClubResolver** - Handles Club queries and club member relationships
2. **PracticionerResolver** - Handles Practicioner queries and membership relationships
3. **ClubMemberResolver** - Handles ClubMember queries and nested object fetching
4. **SeasonPlayerResolver** - Handles SeasonPlayer queries and license information
5. **MatchResolver** - Handles Match queries

Each resolver includes TODO comments where QueryBus integration is needed to fetch actual domain data.

## Configuration

### Dependencies
- Spring GraphQL Starter (`spring-boot-starter-graphql`)
- Domain and Repository artifacts
- QueryBus for command/query pattern integration

### Properties (`application.properties`)
- `spring.graphql.graphiql.enabled=true` - Enable GraphiQL UI at `/graphiql`
- CORS settings for cross-origin requests
- Schema introspection enabled for development

## Usage

### GraphQL Endpoint
The GraphQL endpoint is available at `/graphql` when the runtime module is running.

### Example Queries

```graphql
# Find a club by name
query {
  findClubByName(name: "MyClub") {
    id
    name
    yearRanges
    members {
      id
      practicioner {
        firstName
        secondName
      }
    }
  }
}

# List all season players
query {
  listAllSeasonPlayers {
    id
    yearRange
    clubMember {
      club {
        name
      }
    }
    license {
      id
      tag
    }
  }
}
```

## Integration Points

### QueryBus Integration (TODO)
The resolvers are currently stubbed with TODO comments. To fully implement the GraphQL layer:

1. Inject `SynchronousQueryBus` into each resolver
2. Implement query methods that dispatch domain queries using the QueryBus
3. Map results to GraphQL DTOs before returning

Example pattern (from REST module):
```java
@Autowired
private SynchronousQueryBus queryBus;

public List<ClubGraphQLDto> findClubByName(String name) {
    FindClubByNameQuery query = new FindClubByNameQuery(name);
    Response response = queryBus.execute(query);
    List<?> clubs = response.getPayload();
    return clubs.stream()
        .map(c -> ClubGraphQLDto.fromDomain((Club) c))
        .collect(Collectors.toList());
}
```

### Nested Object Resolution
The `@SchemaMapping` annotations handle fetching related objects. Additional QueryBus integration is needed for:
- Fetching club members for a club
- Fetching practicioner memberships
- Fetching season players for a club member

## Development Notes

### GraphiQL Interface
When running the application, navigate to `/graphiql` to access the interactive GraphQL IDE.

### Schema Validation
The GraphQL schema is automatically validated on startup. Any schema errors will prevent the application from starting.

### Type Mapping
GraphQL type names are mapped to Java records:
- String → GraphQL String
- UUID → GraphQL ID
- Integer → GraphQL Int
- List → GraphQL List
- Record types → GraphQL Object types

## Future Enhancements

1. **Mutations** - Add mutation operations for creating/updating entities
2. **Subscriptions** - Add real-time updates using GraphQL subscriptions
3. **Error Handling** - Implement comprehensive error handling with custom error types
4. **Field Authorization** - Add field-level authorization for sensitive data
5. **Pagination** - Implement cursor-based pagination for large result sets
6. **Filtering & Sorting** - Add advanced filtering and sorting capabilities
7. **Batch Data Loading** - Implement DataLoader for efficient nested queries

## Testing

TODO: Add GraphQL integration tests and resolver unit tests.

