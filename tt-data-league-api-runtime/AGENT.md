# tt-data-league-api-runtime Module - Agentic Development Guide

## Module Purpose

**Application Runtime & Bootstrap** - The Spring Boot application entry point that aggregates all modules and initializes the complete API server. Handles server configuration, dependency wiring, and application startup.

## Module Role in Architecture

```
System Startup
    ↓
[RUNTIME MODULE] ← You are here
    ↓
Initializes All Modules:
├── REST API
├── GraphQL API
├── Core Logic
├── Repository Layer
└── Database Connection
    ↓
Running Application
```

This module is the orchestrator that brings all modules together into a single running Spring Boot application.

## Module Dependencies

### Direct Dependencies
- `tt-data-league-api-rest` - REST API endpoints
- `tt-data-league-api-graphql` - GraphQL API endpoints
- `spring-boot-starter-data-jpa` - JPA persistence
- `spring-boot-starter-jdbc` - Connection pooling
- `spring-boot-starter-security` - Authentication/authorization
- `spring-security-crypto` - Password encoding
- `mysql-connector-j` - MySQL JDBC driver (runtime)
- `postgresql` - PostgreSQL JDBC driver
- `commandbus-synchronous-inmemory` - Command bus
- `querybus-synchronous-inmemory` - Query bus
- `eventbus-synchronous-inmemory` - Event bus
- `commons-core` - Utilities

### Inverse Dependencies
- None (this is the entry point)

## Directory Structure

```
src/main/java/org/cttelsamicsterrassa/data/
├── TtDataLeagueApiRuntimeApplication.java  # Spring Boot main class
├── config/
│   ├── ApplicationConfig.java              # Application-wide configuration
│   ├── DataSourceConfig.java               # Database configuration
│   ├── BusConfiguration.java               # Command/Query/Event bus setup
│   └── CorsConfig.java                     # CORS configuration
└── startup/
    ├── DatabaseInitializer.java            # Database setup
    └── ApplicationStartupListener.java     # Startup events

resources/
├── application.properties                  # Main configuration
├── application-dev.properties             # Development profile
├── application-prod.properties            # Production profile
├── application-test.properties            # Test profile
├── logback.xml                            # Logging configuration
└── schema.sql                             # DDL scripts (optional)
```

## Key Components

### 1. Spring Boot Main Class

**Purpose:** Entry point for Spring Boot application

```java
package org.cttelsamicsterrassa.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "org.cttelsamicsterrassa.data.api.rest",
    "org.cttelsamicsterrassa.data.api.graphql",
    "org.cttelsamicsterrassa.data.api.repository.jpa",
    "org.albertsanso"
})
public class TtDataLeagueApiRuntimeApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TtDataLeagueApiRuntimeApplication.class, args);
    }
}
```

### 2. Application Configuration

Centralizes Spring beans and configuration:

```java
package org.cttelsamicsterrassa.data.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class ApplicationConfig {
    // Central configuration beans
}
```

### 3. Bus Configuration

Sets up Command/Query/Event buses:

```java
package org.cttelsamicsterrassa.data.config;

import org.albertsanso.commons.command.CommandBus;
import org.albertsanso.commons.query.QueryBus;
import org.albertsanso.commons.event.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BusConfiguration {
    
    @Bean
    public CommandBus commandBus() {
        // Initialize in-memory command bus
        return new SynchronousInMemoryCommandBus();
    }
    
    @Bean
    public QueryBus queryBus() {
        // Initialize in-memory query bus
        return new SynchronousInMemoryQueryBus();
    }
    
    @Bean
    public EventBus eventBus() {
        // Initialize in-memory event bus
        return new SynchronousInMemoryEventBus();
    }
}
```

### 4. Database Configuration

```java
package org.cttelsamicsterrassa.data.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }
    
    // DataSource bean configured via application.properties
}
```

### 5. CORS Configuration

```java
package org.cttelsamicsterrassa.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedHeaders("*")
                    .allowCredentials(false)
                    .maxAge(3600);
                    
                registry.addMapping("/graphql")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST")
                    .allowedHeaders("*")
                    .maxAge(3600);
            }
        };
    }
}
```

## Application Properties

### Main Configuration (application.properties)

```properties
# Application
spring.application.name=tt-data-league-api
server.port=8080
server.servlet.context-path=/

# Data Source
spring.datasource.url=jdbc:mysql://localhost:3306/tt_data_league
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# GraphQL
spring.graphql.graphiql.enabled=true
spring.graphql.path=/graphql
spring.graphql.schema.locations=classpath:graphql/

# Logging
logging.level.root=INFO
logging.level.org.cttelsamicsterrassa=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# JWT
jwt.secret=your-super-secret-key-change-in-production
jwt.expiration=3600000

# Server
server.servlet.session.timeout=30m
server.compression.enabled=true
server.compression.min-response-size=1024
```

### Development Profile (application-dev.properties)

```properties
# Development overrides
server.port=8080
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
logging.level.root=DEBUG
logging.level.org.hibernate=DEBUG
jwt.secret=dev-secret-key-not-for-production
```

### Production Profile (application-prod.properties)

```properties
# Production overrides
server.port=8080
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.root=WARN
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=10
jwt.secret=${JWT_SECRET:please-set-via-env-variable}
```

## Startup Process

### Initialization Order

1. **Spring Context Creation**
   - Load application.properties
   - Scan component packages
   - Create bean instances

2. **Database Connection**
   - HikariCP initializes connection pool
   - JPA/Hibernate initializes

3. **Bus Initialization**
   - CommandBus instance created
   - QueryBus instance created
   - EventBus instance created

4. **Component Registration**
   - REST controllers registered
   - GraphQL resolvers registered
   - Repositories initialized
   - Security filters configured

5. **Embedded Server**
   - Tomcat (or other servlet container) starts
   - Routes configured
   - Ready to accept requests

### Startup Code Example

```java
package org.cttelsamicsterrassa.data.startup;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ApplicationStartupListener {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("======================================");
        logger.info("TT Data League API Started Successfully");
        logger.info("REST API: http://localhost:8080/api/v1");
        logger.info("GraphQL: http://localhost:8080/graphql");
        logger.info("GraphiQL: http://localhost:8080/graphiql");
        logger.info("======================================");
    }
}
```

## Server Endpoints

Once running, the application provides:

| Endpoint | Purpose |
|----------|---------|
| `GET /` | Health check |
| `GET /actuator` | Actuator endpoints |
| `GET /api/v1/*` | REST API endpoints |
| `POST /graphql` | GraphQL queries/mutations |
| `GET /graphiql` | GraphQL IDE (if enabled) |

## Important Guidelines

### DO:
✅ Configure all modules in @ComponentScan  
✅ Set spring.jpa.hibernate.ddl-auto appropriately per profile  
✅ Use profiles for different environments  
✅ Configure bus implementations  
✅ Handle startup failures gracefully  
✅ Log startup information  
✅ Set proper security configurations  
✅ Use environment variables for sensitive data  

### DON'T:
❌ Put business logic in main class  
❌ Skip database validation (use validate in prod)  
❌ Hardcode configuration values  
❌ Forget to exclude test dependencies  
❌ Misconfigure @ComponentScan  
❌ Expose sensitive information in logs  
❌ Use create-drop in production  
❌ Skip security configuration  

## Running the Application

### Development
```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Production
```bash
mvn clean package -DskipTests
java -jar target/tt-data-league-api-runtime-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:mysql://prod-db:3306/tt_data_league \
  --spring.datasource.username=admin \
  --spring.datasource.password=$DB_PASSWORD
```

## Health Checks

Add actuator endpoint for monitoring:

```properties
# application.properties
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=when-authorized
```

## Logging Configuration

Logback configuration in `logback.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
    
    <logger name="org.cttelsamicsterrassa" level="DEBUG" />
    <logger name="org.springframework" level="INFO" />
</configuration>
```

## Related Modules

All modules are aggregated here:
- **REST Module** ([tt-data-league-api-rest](../tt-data-league-api-rest/AGENT.md))
- **GraphQL Module** ([tt-data-league-api-graphql](../tt-data-league-api-graphql/AGENT.md))
- **Core Module** ([tt-data-league-api-core](../tt-data-league-api-core/AGENT.md))
- **Repository Module** ([tt-data-league-api-repository-jpa](../tt-data-league-api-repository-jpa/AGENT.md))

