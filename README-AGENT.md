# 📚 TT Data League API - AGENT.md Master Index

## 🎯 Quick Navigation

### 📖 Start Here
- **[AGENT.md](./AGENT.md)** - Project overview and architecture (83 lines)
- **[AGENT-INDEX.md](./AGENT-INDEX.md)** - Quick reference and index (226 lines)

---

## 📋 Module Documentation

### 1. Core Module - Application Logic
**File**: [tt-data-league-api-core/AGENT.md](./tt-data-league-api-core/AGENT.md)  
**Size**: 230 lines  
**Purpose**: Commands, Queries, Handlers for business logic  
**Key Topics**:
- CQRS pattern implementation
- Command/Query creation patterns
- CommandHandler/QueryHandler patterns
- 6 business domains (Club, ClubMember, Match, Practitioner, SeasonPlayer, SeasonPlayerResult)

### 2. REST Module - HTTP API
**File**: [tt-data-league-api-rest/AGENT.md](./tt-data-league-api-rest/AGENT.md)  
**Size**: 285 lines  
**Purpose**: REST endpoints and controllers  
**Key Topics**:
- REST controller patterns
- HTTP request/response handling
- Data Transfer Objects (DTOs)
- Error handling and validation
- Security and JWT authentication
- OpenAPI/Swagger annotations

### 3. GraphQL Module - Alternative API
**File**: [tt-data-league-api-graphql/AGENT.md](./tt-data-league-api-graphql/AGENT.md)  
**Size**: 391 lines  
**Purpose**: GraphQL queries and mutations  
**Key Topics**:
- Query resolver patterns
- Mutation resolver patterns
- GraphQL schema definition
- Data loader optimization
- Field resolver patterns
- Configuration

### 4. Repository Module - Data Persistence
**File**: [tt-data-league-api-repository-jpa/AGENT.md](./tt-data-league-api-repository-jpa/AGENT.md)  
**Size**: 345 lines  
**Purpose**: JPA/Hibernate repositories  
**Key Topics**:
- Spring Data JPA patterns
- Repository interface creation
- Custom @Query implementation
- Query method naming conventions
- Entity mapping
- Database configuration

### 5. Runtime Module - Application Bootstrap
**File**: [tt-data-league-api-runtime/AGENT.md](./tt-data-league-api-runtime/AGENT.md)  
**Size**: 348 lines  
**Purpose**: Spring Boot application initialization  
**Key Topics**:
- Main application class
- Application configuration
- Bus setup (Command/Query/Event)
- Database configuration
- CORS configuration
- application.properties setup

---

## 🗺️ Architecture Map

```
┌─────────────────────────────┐
│     HTTP/GraphQL Clients    │
└──────────────┬──────────────┘
               │
    ┌──────────┴──────────┐
    │                     │
    ▼                     ▼
┌─────────────┐    ┌──────────────┐
│  REST API   │    │  GraphQL API │
│  (Module 2) │    │  (Module 3)  │
└──────┬──────┘    └────────┬─────┘
       │                    │
       └────────┬───────────┘
                │
                ▼
        ┌──────────────────┐
        │  Core Logic      │
        │  (Module 1)      │
        │ Commands/Queries │
        └────────┬─────────┘
                 │
                 ▼
        ┌──────────────────┐
        │  Repository      │
        │  (Module 4)      │
        │  JPA/Hibernate   │
        └────────┬─────────┘
                 │
                 ▼
        ┌──────────────────┐
        │    Database      │
        │ MySQL/PostgreSQL │
        └──────────────────┘

        Runtime Module (5) orchestrates all above
```

---

## 📊 Documentation Statistics

| Component | File | Lines | Topics |
|-----------|------|-------|--------|
| Overview | AGENT.md | 83 | Architecture, patterns, domains |
| Index | AGENT-INDEX.md | 226 | Quick reference, checklists, learning path |
| Core Logic | core/AGENT.md | 230 | Commands, Queries, Handlers |
| REST API | rest/AGENT.md | 285 | Controllers, DTOs, Security |
| GraphQL | graphql/AGENT.md | 391 | Resolvers, Schema, Data Loaders |
| Repository | repository-jpa/AGENT.md | 345 | JPA, Queries, Configuration |
| Runtime | runtime/AGENT.md | 348 | Bootstrap, Config, Startup |
| **TOTAL** | **7 files** | **1,908** | **Complete coverage** |

---

## 🎯 Documentation by Use Case

### I want to understand the project
1. Read: **[AGENT.md](./AGENT.md)**
2. Then: **[AGENT-INDEX.md](./AGENT-INDEX.md)**
3. Time: ~30 minutes

### I need to add a REST endpoint
1. Reference: **[tt-data-league-api-rest/AGENT.md](./tt-data-league-api-rest/AGENT.md)** - REST Controller pattern section
2. Also check: **[tt-data-league-api-core/AGENT.md](./tt-data-league-api-core/AGENT.md)** - for Commands/Queries
3. Time: ~15 minutes per endpoint

### I need to create a GraphQL resolver
1. Reference: **[tt-data-league-api-graphql/AGENT.md](./tt-data-league-api-graphql/AGENT.md)** - Query/Mutation resolver sections
2. Update: schema.graphqls in resources
3. Time: ~15 minutes per resolver

### I need to implement a database query
1. Reference: **[tt-data-league-api-repository-jpa/AGENT.md](./tt-data-league-api-repository-jpa/AGENT.md)** - Repository patterns
2. Add: Query method to repository interface
3. Time: ~10 minutes

### I need to add a new business domain
1. Start: **[AGENT.md](./AGENT.md)** - Architecture overview
2. Core: **[tt-data-league-api-core/AGENT.md](./tt-data-league-api-core/AGENT.md)** - Create commands/handlers
3. REST: **[tt-data-league-api-rest/AGENT.md](./tt-data-league-api-rest/AGENT.md)** - Create controller
4. GraphQL: **[tt-data-league-api-graphql/AGENT.md](./tt-data-league-api-graphql/AGENT.md)** - Create resolver
5. Repository: **[tt-data-league-api-repository-jpa/AGENT.md](./tt-data-league-api-repository-jpa/AGENT.md)** - Create repository
6. Time: ~2 hours for complete implementation

### I need to configure the application
1. Reference: **[tt-data-league-api-runtime/AGENT.md](./tt-data-league-api-runtime/AGENT.md)** - Configuration section
2. Update: application.properties files
3. Time: ~15-30 minutes depending on complexity

---

## 🔍 Search by Topic

### Security & Authentication
→ **[tt-data-league-api-rest/AGENT.md](./tt-data-league-api-rest/AGENT.md)** - Security Configuration section  
→ **[tt-data-league-api-runtime/AGENT.md](./tt-data-league-api-runtime/AGENT.md)** - Security Features section

### Database & Persistence
→ **[tt-data-league-api-repository-jpa/AGENT.md](./tt-data-league-api-repository-jpa/AGENT.md)** - Everything  
→ **[tt-data-league-api-runtime/AGENT.md](./tt-data-league-api-runtime/AGENT.md)** - Database Configuration section

### REST API
→ **[tt-data-league-api-rest/AGENT.md](./tt-data-league-api-rest/AGENT.md)** - Everything  
→ **[AGENT-INDEX.md](./AGENT-INDEX.md)** - Quick reference section

### GraphQL API
→ **[tt-data-league-api-graphql/AGENT.md](./tt-data-league-api-graphql/AGENT.md)** - Everything  
→ **[AGENT-INDEX.md](./AGENT-INDEX.md)** - Quick reference section

### CQRS Pattern
→ **[AGENT.md](./AGENT.md)** - Architecture Pattern section  
→ **[tt-data-league-api-core/AGENT.md](./tt-data-league-api-core/AGENT.md)** - Everything  
→ **[AGENT-INDEX.md](./AGENT-INDEX.md)** - Architecture Quick Reference

### Error Handling
→ **[tt-data-league-api-rest/AGENT.md](./tt-data-league-api-rest/AGENT.md)** - Error Handling section  
→ **[tt-data-league-api-graphql/AGENT.md](./tt-data-league-api-graphql/AGENT.md)** - Important Guidelines section

### Testing
→ Each module's AGENT.md - Testing Patterns section

### Performance Optimization
→ **[tt-data-league-api-repository-jpa/AGENT.md](./tt-data-league-api-repository-jpa/AGENT.md)** - Performance Optimization section  
→ **[tt-data-league-api-graphql/AGENT.md](./tt-data-league-api-graphql/AGENT.md)** - Data Loaders section

### Configuration
→ **[tt-data-league-api-runtime/AGENT.md](./tt-data-league-api-runtime/AGENT.md)** - Application Properties & configuration sections

---

## 📚 Business Domains Covered

All 6 domains have CQRS patterns documented:

1. **Club Management**
   - Commands: Create, Modify, Delete
   - Queries: FindAll, FindById, FindByName, FindBySimilarName

2. **Club Member Management**
   - Similar command/query structure

3. **Match Management**
   - Similar command/query structure

4. **Practitioner Management**
   - Similar command/query structure

5. **Season Player Management**
   - Similar command/query structure

6. **Season Player Result Management**
   - Similar command/query structure

See **[tt-data-league-api-core/AGENT.md](./tt-data-league-api-core/AGENT.md)** for complete details on all domains.

---

## 🚀 Implementation Workflow

1. **Understand the architecture**
   - Read: [AGENT.md](./AGENT.md)

2. **Plan your changes**
   - Reference: [AGENT-INDEX.md](./AGENT-INDEX.md) Implementation Checklist

3. **Implement core logic**
   - Follow: [tt-data-league-api-core/AGENT.md](./tt-data-league-api-core/AGENT.md)

4. **Add repository layer**
   - Follow: [tt-data-league-api-repository-jpa/AGENT.md](./tt-data-league-api-repository-jpa/AGENT.md)

5. **Create REST endpoint** (if needed)
   - Follow: [tt-data-league-api-rest/AGENT.md](./tt-data-league-api-rest/AGENT.md)

6. **Create GraphQL resolver** (if needed)
   - Follow: [tt-data-league-api-graphql/AGENT.md](./tt-data-league-api-graphql/AGENT.md)

7. **Configure if needed**
   - Follow: [tt-data-league-api-runtime/AGENT.md](./tt-data-league-api-runtime/AGENT.md)

8. **Validate**
   - Check: DO/DON'T sections in each AGENT.md

---

## 💡 Tips for Using These Documents

1. **Bookmark the index** - [AGENT-INDEX.md](./AGENT-INDEX.md) is your quick reference
2. **Use Ctrl+F** - Search within each document for specific patterns
3. **Follow templates exactly** - Code templates are tested and proven
4. **Cross-reference modules** - Check dependencies in related modules
5. **Review DO/DON'T** - These prevent common mistakes
6. **Check examples** - Each section has working code examples

---

## 📞 Quick Links

| Need | File |
|------|------|
| Project overview | [AGENT.md](./AGENT.md) |
| Quick reference | [AGENT-INDEX.md](./AGENT-INDEX.md) |
| Commands & Handlers | [core/AGENT.md](./tt-data-league-api-core/AGENT.md) |
| REST endpoints | [rest/AGENT.md](./tt-data-league-api-rest/AGENT.md) |
| GraphQL | [graphql/AGENT.md](./tt-data-league-api-graphql/AGENT.md) |
| Repositories & ORM | [repository-jpa/AGENT.md](./tt-data-league-api-repository-jpa/AGENT.md) |
| Application setup | [runtime/AGENT.md](./tt-data-league-api-runtime/AGENT.md) |

---

## ✅ Checklist for New Developers/Agents

- [ ] Read AGENT.md (root) - 10 min
- [ ] Skim AGENT-INDEX.md - 5 min
- [ ] Read module-specific AGENT.md for your task - 15-20 min
- [ ] Review code templates - 5 min
- [ ] Scan DO/DON'T sections - 5 min
- [ ] Ready to implement! ✅

**Estimated learning time: 40-50 minutes to be productive**

---

## 🎓 Learning Paths

### Path A: Understanding the Architecture
1. AGENT.md (root)
2. AGENT-INDEX.md
3. Skim all module AGENT.md files
4. **Time**: 1-2 hours

### Path B: Quick Start - Add a Feature
1. AGENT-INDEX.md (choose your task)
2. Specific module AGENT.md
3. Follow template
4. **Time**: 30-60 minutes per feature

### Path C: Complete Deep Dive
1. All 7 AGENT.md files
2. Review all code templates
3. Understand all patterns
4. **Time**: 4-6 hours

---

## 📝 File Manifest

```
C:\git\tt-data-league-api\
├── AGENT.md                                    (Project overview - 83 lines)
├── AGENT-INDEX.md                             (Quick reference - 226 lines)
├── tt-data-league-api-core\
│   └── AGENT.md                              (Core logic - 230 lines)
├── tt-data-league-api-rest\
│   └── AGENT.md                              (REST API - 285 lines)
├── tt-data-league-api-graphql\
│   └── AGENT.md                              (GraphQL - 391 lines)
├── tt-data-league-api-repository-jpa\
│   └── AGENT.md                              (Repositories - 345 lines)
└── tt-data-league-api-runtime\
    └── AGENT.md                              (Runtime - 348 lines)

Total: 7 files, 1,908 lines of comprehensive documentation
```

---

## 🎉 Ready to Start!

You now have everything needed to:
- ✅ Understand the architecture
- ✅ Add new features
- ✅ Create endpoints (REST & GraphQL)
- ✅ Implement business logic
- ✅ Configure the application
- ✅ Make informed decisions about code changes

**Pick your starting file above and begin!**

---

**Last Updated**: 2026-03-23  
**Project**: tt-data-league-api  
**Status**: ✅ Complete - Ready for Development

