# TT Data League API - Agentic Development Guide

## Project Overview

**tt-data-league-api** is a table tennis league data management API built with a **multi-module Maven architecture** using Clean Architecture principles and CQRS (Command Query Responsibility Segregation) pattern.

### Project Structure

```
tt-data-league-api/
├── tt-data-league-api-core          # Core application logic (Commands/Queries)
├── tt-data-league-api-rest          # REST API endpoints
├── tt-data-league-api-graphql       # GraphQL API endpoints
├── tt-data-league-api-repository-jpa # JPA/Hibernate persistence layer
├── tt-data-league-api-runtime       # Spring Boot application entry point
└── pom.xml                           # Parent Maven POM
```

## Architecture Pattern

This project follows **Clean Architecture** with **CQRS (Command Query Responsibility Segregation)**:

- **Commands**: Operations that modify state (Create, Update, Delete)
- **Queries**: Operations that read state without side effects
- **Command Handlers**: Execute commands and publish domain events
- **Query Handlers**: Execute queries and return results
- **Bus Pattern**: Central orchestrator for dispatching commands and queries

## Key Technologies

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 21 |
| Build Tool | Maven | - |
| Framework | Spring Boot | 3.5.8 |
| Web API | Spring Web & GraphQL | - |
| Database | JPA/Hibernate, MySQL, PostgreSQL | - |
| Security | Spring Security, JWT | 0.12.5 |
| Bus Framework | albertsanso commons | 0.0.1-SNAPSHOT |

## Domain Entities

The system manages:
- **Club**: Table tennis clubs
- **Club Member**: Members of clubs
- **Practitioner**: League players
- **Match**: League matches
- **Season Player**: Player participation in seasons
- **Season Player Result**: Match results for season players

## Agentic Development Guidelines

### General Principles

1. **Preserve Architecture**: Maintain Clean Architecture and CQRS patterns
2. **Module Isolation**: Keep module dependencies clear (see module-specific guides)
3. **Command/Query Separation**: Commands for writes, Queries for reads
4. **Bus Usage**: Always use CommandBus and QueryBus for operations
5. **DTO Transformation**: Use DTOs in REST/GraphQL layers
6. **Testing**: Maintain test coverage for handlers and controllers

### Code Generation Tasks

When writing code for this project:

1. **New Commands/Queries**: Create in `core` module under appropriate domain folder
2. **New Handlers**: Implement in `core` module, follow naming convention `*CommandHandler` or `*QueryHandler`
3. **New REST Endpoints**: Create controllers in `rest` module, inject CommandBus/QueryBus
4. **New GraphQL Resolvers**: Create in `graphql` module
5. **New Entities**: Create in domain core project (tt-data-league-core-domain)
6. **New Repositories**: Implement in `repository-jpa` module

### Dependency Directions

```
REST Layer
  ↓
Core Layer (Commands/Queries)
  ↓
Repository Layer (JPA)
  ↓
Domain Model (External)

GraphQL Layer → Core Layer → Repository Layer → Domain Model
```

### Common Patterns

**Command Dispatch Pattern:**
```java
CreateClubCommand cmd = new CreateClubCommand(id, name, yearRanges);
DomainCommandResponse response = commandBus.execute(cmd);
```

**Query Pattern:**
```java
FindClubByIdQuery query = new FindClubByIdQuery(clubId);
DomainQueryResponse response = queryBus.ask(query);
Club result = response.getResult();
```

## See Also

- [tt-data-league-api-core](./tt-data-league-api-core/AGENT.md) - Application logic layer
- [tt-data-league-api-rest](./tt-data-league-api-rest/AGENT.md) - REST API layer
- [tt-data-league-api-graphql](./tt-data-league-api-graphql/AGENT.md) - GraphQL API layer
- [tt-data-league-api-repository-jpa](./tt-data-league-api-repository-jpa/AGENT.md) - Persistence layer
- [tt-data-league-api-runtime](./tt-data-league-api-runtime/AGENT.md) - Application entry point

