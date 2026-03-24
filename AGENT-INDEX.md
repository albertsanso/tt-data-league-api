# TT Data League API - AGENT.md Index & Quick Reference

## 📚 Documentation Files Created

### Root Documentation
- **[AGENT.md](./AGENT.md)** - Project overview, architecture, and global patterns

### Module-Specific Guides
1. **[tt-data-league-api-core/AGENT.md](./tt-data-league-api-core/AGENT.md)**
   - Application logic layer with Commands/Queries
   - CQRS handler implementations

2. **[tt-data-league-api-rest/AGENT.md](./tt-data-league-api-rest/AGENT.md)**
   - REST API endpoints and controllers
   - HTTP request/response handling
   - Security and JWT authentication

3. **[tt-data-league-api-graphql/AGENT.md](./tt-data-league-api-graphql/AGENT.md)**
   - GraphQL queries and mutations
   - Resolver implementations
   - Schema definitions

4. **[tt-data-league-api-repository-jpa/AGENT.md](./tt-data-league-api-repository-jpa/AGENT.md)**
   - Data persistence with JPA/Hibernate
   - Repository interfaces and implementations
   - Database configuration

5. **[tt-data-league-api-runtime/AGENT.md](./tt-data-league-api-runtime/AGENT.md)**
   - Spring Boot application bootstrap
   - Central configuration
   - Application startup and initialization

## 🏗️ Architecture Quick Reference

```
┌─────────────────────────────────────────────┐
│          HTTP Clients / GraphQL Clients      │
└────────────────┬────────────────────────────┘
                 │
    ┌────────────┴──────────────┐
    │                           │
    ▼                           ▼
┌──────────────┐        ┌──────────────┐
│  REST Layer  │        │GraphQL Layer │
│ (Controllers)│        │ (Resolvers)  │
└──────┬───────┘        └──────┬───────┘
       │                       │
       └───────────┬───────────┘
                   │
                   ▼
        ┌─────────────────────┐
        │   Core Module       │
        │ Commands/Queries    │
        │  & Handlers         │
        └──────────┬──────────┘
                   │
                   ▼
        ┌─────────────────────┐
        │  Repository Layer   │
        │  (JPA/Hibernate)    │
        └──────────┬──────────┘
                   │
                   ▼
        ┌─────────────────────┐
        │     Database        │
        │(MySQL/PostgreSQL)   │
        └─────────────────────┘
```

## 🚀 Quick Start for Agents

### Creating a New Domain Entity with Full CRUD

1. **Read Core Documentation**
   - Start: [AGENT.md](./AGENT.md) for architecture overview
   - Then: [tt-data-league-api-core/AGENT.md](./tt-data-league-api-core/AGENT.md)

2. **Create Commands & Handlers**
   - Pattern: `Core Module → Commands → Handlers`
   - Template provided in core AGENT.md
   - Location: `tt-data-league-api-core/src/main/java/org/cttelsamicsterrassa/data/api/core/{domain}/{operation}/application/`

3. **Create Repository**
   - Pattern: `Spring Data JPA Interface`
   - Template provided in repository AGENT.md
   - Location: `tt-data-league-api-repository-jpa/src/main/java/org/cttelsamicsterrassa/data/api/repository/jpa/{domain}/`

4. **Create REST Endpoints**
   - Pattern: `Controller → CommandBus/QueryBus`
   - Template provided in REST AGENT.md
   - Location: `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/{domain}/`

5. **Create GraphQL Resolvers**
   - Pattern: `Resolver → CommandBus/QueryBus`
   - Template provided in GraphQL AGENT.md
   - Location: `tt-data-league-api-graphql/src/main/java/org/cttelsamicsterrassa/data/api/graphql/{domain}/`

## 📋 Supported Operations by Domain

All domains support these CQRS operations:

### Commands (Writes)
- `Create{Entity}Command` - Create new entity
- `Modify{Entity}Command` - Update existing entity
- `Delete{Entity}Command` - Delete entity

### Queries (Reads)
- `FindAll{Entities}Query` - Get all entities
- `Find{Entity}ByIdQuery` - Get by UUID
- `Find{Entity}ByNameQuery` - Get by name
- `Find{Entity}BySimilarNameQuery` - Fuzzy search by name

## 📖 Documentation Structure by Purpose

### For Understanding Architecture
1. Read [AGENT.md](./AGENT.md) - Overall design
2. Review dependency diagram in architecture section

### For Writing Command Handlers
→ [tt-data-league-api-core/AGENT.md](./tt-data-league-api-core/AGENT.md)
- Command class pattern
- CommandHandler implementation
- Key guidelines

### For Creating REST Endpoints
→ [tt-data-league-api-rest/AGENT.md](./tt-data-league-api-rest/AGENT.md)
- REST Controller pattern
- DTO definitions
- OpenAPI/Swagger annotations
- Error handling

### For Building GraphQL API
→ [tt-data-league-api-graphql/AGENT.md](./tt-data-league-api-graphql/AGENT.md)
- Query Resolver pattern
- Mutation Resolver pattern
- Schema.graphqls definition
- Data loader optimization

### For Database Operations
→ [tt-data-league-api-repository-jpa/AGENT.md](./tt-data-league-api-repository-jpa/AGENT.md)
- Repository interface pattern
- Custom @Query annotations
- Query method naming
- Entity mapping

### For Application Configuration
→ [tt-data-league-api-runtime/AGENT.md](./tt-data-league-api-runtime/AGENT.md)
- Main application class
- Configuration beans
- application.properties setup
- Startup process

## 🎯 Common Development Tasks

### Add a new REST endpoint for existing entity
1. Open: [tt-data-league-api-rest/AGENT.md](./tt-data-league-api-rest/AGENT.md)
2. Follow: REST Controller pattern
3. Inject: CommandBus/QueryBus
4. Dispatch: Appropriate Command/Query

### Add a new GraphQL mutation
1. Open: [tt-data-league-api-graphql/AGENT.md](./tt-data-league-api-graphql/AGENT.md)
2. Follow: Mutation Resolver pattern
3. Update: schema.graphqls
4. Test: GraphQL query

### Add a new database query
1. Open: [tt-data-league-api-repository-jpa/AGENT.md](./tt-data-league-api-repository-jpa/AGENT.md)
2. Add: Query method to repository interface
3. Or use: @Query annotation for complex queries

### Handle new business requirement
1. Start: [tt-data-league-api-core/AGENT.md](./tt-data-league-api-core/AGENT.md)
2. Create: New Command or Query class
3. Create: Corresponding Handler
4. Expose: Via REST or GraphQL

## 🔐 Security Patterns

**Authentication**: JWT token-based (see REST AGENT.md)
**Authorization**: @PreAuthorize annotations
**Location**: Security config in runtime module

## 💾 Database Configuration

**Supported Databases**:
- MySQL (default in dev)
- PostgreSQL (available)

**Connection Pool**: HikariCP
**Configuration**: See runtime/AGENT.md

## 📝 Code Templates Provided

All AGENT.md files include working code templates for:
- Command classes
- CommandHandlers
- Query classes
- QueryHandlers
- REST Controllers
- REST DTOs
- GraphQL Resolvers
- GraphQL Schemas
- Repository Interfaces
- Configuration Classes
- Exception Handlers

## ✅ Implementation Checklist

When adding new functionality:

- [ ] Read the root AGENT.md
- [ ] Read the module-specific AGENT.md
- [ ] Follow code patterns exactly
- [ ] Update all relevant layers (Core → Repository → REST/GraphQL)
- [ ] Add validation annotations
- [ ] Handle exceptions properly
- [ ] Document with OpenAPI/Swagger
- [ ] Update GraphQL schema
- [ ] Write tests
- [ ] Review DO/DON'T guidelines

## 🔗 Cross-Module Dependencies

```
REST Module
  depends on → Core Module
                depends on → Repository Module
                              depends on → External Domain Model

GraphQL Module
  depends on → Core Module
                depends on → Repository Module
                              depends on → External Domain Model

Runtime Module
  depends on → REST Module & GraphQL Module & Repository Module
```

## 📞 Finding Specific Information

| Need | Location |
|------|----------|
| Architecture overview | [AGENT.md](./AGENT.md) |
| Command/Query patterns | [core/AGENT.md](./tt-data-league-api-core/AGENT.md) |
| REST endpoint patterns | [rest/AGENT.md](./tt-data-league-api-rest/AGENT.md) |
| GraphQL patterns | [graphql/AGENT.md](./tt-data-league-api-graphql/AGENT.md) |
| Database/ORM patterns | [repository-jpa/AGENT.md](./tt-data-league-api-repository-jpa/AGENT.md) |
| Configuration patterns | [runtime/AGENT.md](./tt-data-league-api-runtime/AGENT.md) |
| Security config | [rest/AGENT.md](./tt-data-league-api-rest/AGENT.md) & [runtime/AGENT.md](./tt-data-league-api-runtime/AGENT.md) |
| Testing patterns | Each module's AGENT.md (Testing Patterns section) |

## 🎓 Learning Path

**New to this project?**
1. Start with [AGENT.md](./AGENT.md) - 10 min read
2. Pick a module that interests you
3. Read its AGENT.md file - 15-20 min
4. Review code templates
5. Try creating a simple feature

**Want to add a feature?**
1. Identify which layers are affected
2. Open relevant AGENT.md files
3. Follow the provided patterns
4. Reference DO/DON'T sections
5. Cross-check with architecture diagram

**Troubleshooting?**
1. Check the DO/DON'T sections
2. Review related modules for dependencies
3. Verify you're following the right pattern
4. Check the architecture diagram for dependency direction

---

**Last Updated**: 2026-03-23  
**Project**: tt-data-league-api (Table Tennis League API)  
**Architecture**: Clean Architecture + CQRS  
**Status**: ✅ Complete - Ready for Agentic Development

