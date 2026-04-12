# Build Plan
> FEAT-002: Add REST endpoint `/practicioner/find_by_similar_name?name=<fragment>` for case-insensitive, partial fullName search.

1. Verify query/repository contract
   - Confirm `FindPracticionerByNameQuery` exists in core and validate expected behavior for case-insensitive partial matching.
   - Confirm external repository contract supports similar-name lookup; if not, document fallback or dependency update requirement.
2. Implement core query handler behavior
   - Update `FindPracticionerByNameQueryHandler` to call repository lookup with the `name` fragment.
   - Return `DomainQueryResponse.sucessResponse(...)` with empty list when no matches (avoid null propagation).
3. Expose REST endpoint in rest module
   - Add `GET /api/v1/practicioner/find_by_similar_name` in `PracticionerController` and OpenAPI interface/controller contract.
   - Map `name` query param to `FindPracticionerByNameQuery` and convert domain output to `PracticionerDto` list.
4. Add request validation and error behavior
   - Enforce required `name` param.
   - Reject blank/whitespace-only `name` with `400 Bad Request`.
5. Add tests
   - Core unit test: handler calls repository correctly and returns expected response for match/no-match scenarios.
   - REST tests: success, missing `name`, blank `name`, and case-insensitive partial match behavior.
   - OpenAPI integration test: assert endpoint path and query parameter appear in `/v3/api-docs`.
6. Update generated API contract/docs
   - Regenerate `openapi.yaml` using project script workflow.
   - Validate OpenAPI output and ensure endpoint documentation includes parameter, response schema, and error response.
7. Finish feature bookkeeping
   - Update `docs/sdd/FEATURES.md` status transition when implementation starts/completes (`ready` -> `in-progress` -> `done`).

# Implementation Guidelines

- Keep path naming aligned with existing project conventions (`practicioner`) to avoid breaking consistency.
- Prefer CQRS flow already used in project: REST -> QueryBus -> QueryHandler -> Repository.
- Keep controller thin: validation + mapping only; business filtering must remain in handler/repository.
- Use existing DTO mapping style in rest module and avoid introducing duplicate mapping logic.
- Include Springdoc annotations in endpoint definitions so generated OpenAPI remains accurate.

# Notes

- Potential naming risk: endpoint uses `practicioner` (typo) by convention; decide whether to add a `practitioner` alias later for client ergonomics.
- If repository contract cannot perform case-insensitive partial search directly, coordinate with `tt-data-league-core-domain` dependency before finalizing implementation.
- Suggested validation policy: treat missing or blank `name` as client error (`400`) to prevent broad, expensive scans.

