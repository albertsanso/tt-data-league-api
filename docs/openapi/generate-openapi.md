# Generating OpenAPI for tt-data-league-api

This document explains two approaches to produce an OpenAPI contract for the REST module.

1) Runtime generation with `springdoc-openapi` (recommended):

- Add dependency to your `tt-data-league-api-rest/pom.xml`:

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-ui</artifactId>
  <version>1.7.0</version> <!-- pick a compatible latest version -->
</dependency>
```

- Start the application and visit `http://localhost:8080/swagger-ui.html` or `http://localhost:8080/v3/api-docs` to fetch the live OpenAPI JSON/YAML.

2) Static inference (what we did):

- We generated `openapi.yaml` at repository root by scanning controllers and DTOs.
- This file is a starting point and should be validated and extended with descriptions, examples and required fields.

Validation:

- Use `swagger-cli` or `openapi-generator` to validate the file locally.

```bash
# with npm installed
npm install -g @apidevtools/swagger-cli
swagger-cli validate openapi.yaml
```

Serve via Swagger UI (Docker): see `docker/swagger-ui/docker-compose.yml`.

Next steps:
- Add proper examples and mark fields required according to domain invariants.
- Consider adding `@Parameter` and `@Operation` annotations in controllers to improve generated docs when using springdoc.
- Automate OpenAPI generation in CI using `swagger-cli` validation and fail on invalid specs.

