# tt-data-league-api-repository-jpa Module - Agentic Development Guide

## Module Purpose

**Persistence Layer** - Implements data access through JPA/Hibernate ORM. Provides repository implementations for domain entities, managing database operations and entity lifecycle.

## Module Role in Architecture

```
Core Module (Commands/Queries)
        ↓
  [REPOSITORIES] ← You are here
        ↓
   JPA/Hibernate
        ↓
   Database (MySQL, PostgreSQL)
```

This module abstracts database operations and provides a clean repository interface for handlers in the core module.

## Module Dependencies

### Direct Dependencies
- `tt-data-league-core-domain` - Domain models and entities
- `tt-data-league-core-repository-jpa` (external) - Repository contracts/interfaces
- `spring-boot-starter-data-jpa` - Spring Data JPA
- `spring-boot-starter-jdbc` - JDBC for connection pooling
- `HikariCP` (v4+) - Connection pooling
- `mysql-connector-j` - MySQL JDBC driver
- `postgresql` - PostgreSQL JDBC driver
- `commandbus-synchronous-inmemory` - Command bus for domain events
- `querybus-synchronous-inmemory` - Query bus
- `eventbus-synchronous-inmemory` - Event bus for domain events
- `commons-core` - Utility interfaces
- Lombok - Code generation
- Spring Boot starter

### Inverse Dependencies
- `tt-data-league-api-core` - Uses repositories to persist/retrieve entities
- `tt-data-league-api-graphql` - May directly access repositories

## Directory Structure

```
src/main/java/org/cttelsamicsterrassa/data/api/repository/
├── jpa/
│   ├── club/
│   │   ├── ClubJpaRepository.java          # Spring Data JPA interface
│   │   ├── Club{Entity}JpaRepository.java  # Custom repository implementation
│   │   └── ClubRepository.java             # Repository interface (contract)
│   │
│   ├── club_member/                       # Similar structure
│   ├── match/
│   ├── practicioner/
│   ├── season_player/
│   └── season_player_result/
│
└── shared/
    ├── BaseRepository.java                # Abstract base class
    ├── JpaConfig.java                     # JPA configuration
    └── DatabaseConfig.java                # Database connection config

resources/
└── (database migrations if using Flyway/Liquibase)
```

## Core Concepts

### Repository Pattern

Repositories act as intermediaries between core logic and persistence:

```
Core Module
    ↓
Interface: ClubRepository
    ↓
Implementation: ClubJpaRepository (extends JpaRepository)
    ↓
JPA/Hibernate
    ↓
Database
```

### Spring Data JPA Interface

Spring provides `JpaRepository<T, ID>` which offers CRUD operations:
- `save(T entity)`
- `findById(ID id)`
- `findAll()`
- `delete(T entity)`
- `deleteById(ID id)`
- `count()`

### Custom Queries

For complex queries, use:
- `@Query` annotations
- Query methods with specific naming conventions
- Custom implementations

## Code Patterns

### Creating a Repository Interface

```java
package org.cttelsamicsterrassa.data.api.repository.jpa.{domain};

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.cttelsamicsterrassa.data.core.domain.model.{Entity};

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface {Entity}Repository extends JpaRepository<{Entity}, UUID> {
    
    /**
     * Find {entity} by name
     * @param name the {entity} name
     * @return Optional containing {entity} if found
     */
    Optional<{Entity}> findByName(String name);
    
    /**
     * Find {entities} with similar name pattern
     * @param name partial name pattern
     * @return List of matching {entities}
     */
    @Query("SELECT e FROM {Entity} e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<{Entity}> findBySimilarName(@Param("name") String name);
    
    /**
     * Find all {entities} ordered by creation date
     * @return List of all {entities}
     */
    @Query("SELECT e FROM {Entity} e ORDER BY e.createdAt DESC")
    List<{Entity}> findAllOrderByCreatedAtDesc();
    
    /**
     * Check if {entity} exists by name
     * @param name the {entity} name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);
}
```

### Standard Query Method Naming Conventions

Spring Data JPA parses method names to generate queries:

```java
// Single entity queries
Optional<Entity> findById(UUID id);
Optional<Entity> findByName(String name);
Entity findByNameAndStatus(String name, String status);

// List queries
List<Entity> findAll();
List<Entity> findByStatus(String status);
List<Entity> findByNameContainingIgnoreCase(String name);
List<Entity> findByCreatedAfter(LocalDateTime date);

// Boolean queries
boolean existsById(UUID id);
boolean existsByName(String name);

// Count queries
long countByStatus(String status);

// Delete queries
void deleteByStatus(String status);
void deleteByIdGreaterThan(UUID id);
```

### Custom Repository Implementation

```java
package org.cttelsamicsterrassa.data.api.repository.jpa.{domain};

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.cttelsamicsterrassa.data.core.domain.model.{Entity};

import java.util.List;
import java.util.UUID;

@Repository
public class {Entity}RepositoryCustom {
    
    private final EntityManager entityManager;
    
    public {Entity}RepositoryCustom(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * Complex query for {entities} with multiple criteria
     */
    public List<{Entity}> findByComplexCriteria(String name, String status, int minValue) {
        String jpql = """
            SELECT e FROM {Entity} e 
            WHERE e.name LIKE :name 
            AND e.status = :status 
            AND e.value >= :minValue
            ORDER BY e.createdAt DESC
            """;
        
        return entityManager.createQuery(jpql, {Entity}.class)
            .setParameter("name", "%" + name + "%")
            .setParameter("status", status)
            .setParameter("minValue", minValue)
            .getResultList();
    }
}
```

### JPA Configuration

```java
package org.cttelsamicsterrassa.data.api.repository.jpa.shared;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
    basePackages = "org.cttelsamicsterrassa.data.api.repository.jpa"
)
@EnableTransactionManagement
public class JpaConfig {
    // JPA configuration beans
}
```

### Database Configuration

```java
package org.cttelsamicsterrassa.data.api.repository.jpa.shared;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    
    @Value("${spring.datasource.url}")
    private String dbUrl;
    
    @Value("${spring.datasource.username}")
    private String dbUsername;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;
    
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        return new HikariDataSource(config);
    }
}
```

## Important Guidelines

### DO:
✅ Extend `JpaRepository<T, ID>` in repository interfaces  
✅ Use `@Repository` annotation  
✅ Use Spring Data method naming conventions  
✅ Use `@Query` for complex queries  
✅ Use `@Transactional` for operations that modify data  
✅ Use Optional<T> for queries that might return null  
✅ Create indexes for frequently queried fields  
✅ Handle `EntityNotFoundException` appropriately  

### DON'T:
❌ Create repositories for every query (use query methods)  
❌ Use native SQL unless absolutely necessary  
❌ Expose domain entities directly from repository layer  
❌ Create circular dependencies between repositories  
❌ Forget to use @Transactional for writes  
❌ Write N+1 query problems (use fetch joins)  
❌ Put business logic in repositories  
❌ Use repository methods directly in controllers (go through core)  

## Database Schema Considerations

### Entity Mapping Best Practices

```java
package org.cttelsamicsterrassa.data.core.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clubs")
@Data
public class Club {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @ElementCollection
    @CollectionTable(name = "club_year_ranges")
    @Column(name = "year_range")
    private List<String> yearRanges;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<ClubMember> members;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

## Connection Pool Configuration

HikariCP settings in `application.properties`:
```properties
# HikariCP Configuration
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

## Testing Patterns

Test repositories with:
1. `@DataJpaTest` for isolated testing
2. Embedded H2 database for testing
3. Test data builders
4. Assert repository operations

## Performance Optimization

### Fetch Strategies

Use eager/lazy loading appropriately:
```java
@OneToMany(fetch = FetchType.LAZY)  // Default
@OneToMany(fetch = FetchType.EAGER) // Load immediately
```

### Query Optimization

Use fetch joins for related data:
```java
@Query("""
    SELECT DISTINCT c FROM Club c 
    LEFT JOIN FETCH c.members 
    WHERE c.id = :id
    """)
Optional<Club> findByIdWithMembers(@Param("id") UUID id);
```

### Pagination

For large result sets use pagination:
```java
@Query("SELECT e FROM Entity e")
Page<Entity> findAll(Pageable pageable);
```

## Related Modules

- **Core Layer** ([tt-data-league-api-core](../tt-data-league-api-core/AGENT.md)): Calls repositories for persistence
- **REST Layer** ([tt-data-league-api-rest](../tt-data-league-api-rest/AGENT.md)): Controllers dispatch commands that use repositories
- **GraphQL Layer** ([tt-data-league-api-graphql](../tt-data-league-api-graphql/AGENT.md)): Resolvers dispatch commands/queries that use repositories
- **Domain** (External): Core domain entities mapped by JPA

