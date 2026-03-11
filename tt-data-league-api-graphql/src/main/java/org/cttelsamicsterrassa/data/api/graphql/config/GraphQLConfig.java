package org.cttelsamicsterrassa.data.api.graphql.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.GraphQlSource;

/**
 * GraphQL configuration for the tt-data-league API.
 * This configuration class sets up the GraphQL environment and registers resolvers.
 */
@Configuration
public class GraphQLConfig {

    // GraphQL schema is loaded automatically from src/main/resources/graphql/schema.graphqls
    // Resolvers are registered automatically via @Controller and @QueryMapping annotations
    // Spring Boot 3.5+ handles this configuration automatically with spring-boot-starter-graphql
}

