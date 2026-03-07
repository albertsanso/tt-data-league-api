# openapi.yaml (inferred)

This repository contains an inferred OpenAPI 3.0.3 specification at `openapi.yaml` generated from the `tt-data-league-api-rest` module source.

What it contains:
- Paths and operations inferred from controller classes.
- Component schemas built from Java `record` DTO types present in the REST module.
- Example payloads and `required` hints for commonly required fields.

How to use:
- Validate: `swagger-cli validate openapi.yaml` (install `@apidevtools/swagger-cli` via npm), or paste into the online Swagger Editor.
- Serve locally: Use the included Docker Compose to run Swagger UI and point it at `openapi.yaml`.

Limitations:
- This is an inferred spec — it may miss authentication requirements, advanced parameter formats, and some required/optional semantics.
- Prefer runtime generation with `springdoc-openapi` for always-up-to-date docs.

Next improvements (low-effort):
- Add `@Operation` Javadoc/annotations on controllers for richer descriptions.
- Add schemas for error responses and pagination.
- Add examples for each endpoint's request and response.

