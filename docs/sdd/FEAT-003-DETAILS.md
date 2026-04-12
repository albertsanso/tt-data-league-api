# Build Plan

1. Confirm FEAT-003 scope and endpoint contract baselines across REST and core modules.
   - Review current controllers: `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/club_member/ClubMemberController.java`, `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/season_player/SeasonPlayerController.java`, `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/season_player_result/SeasonPlayerResultController.java`, `tt-data-league-api-rest/src/main/java/org/cttelsamicsterrassa/data/api/rest/match/MatchController.java`.
   - Freeze path conventions already used in this repo (`/api/v1/club_member`, `/api/v1/season_player`, `/api/v1/player_result`, `/api/v1/match`) to avoid breaking clients.
   - Define final missing REST operations for this feature only: ClubMember (`findById`, `modify`, `delete`), SeasonPlayer (`modify`), SeasonPlayerResult (`findById`, `modify`, `delete`), Match (`findById` real implementation, `modify`, `delete`).

2. Add missing CQRS write-side artifacts in `tt-data-league-api-core` for targeted entities.
   - ClubMember: add `modify` and `delete` packages under `tt-data-league-api-core/src/main/java/org/cttelsamicsterrassa/data/api/core/club_member/` with command + handler symbols (e.g., `ModifyClubMemberCommand`, `ModifyClubMemberCommandHandler`, `DeleteClubMemberCommand`, `DeleteClubMemberCommandHandler`).
   - SeasonPlayer: add `modify` package under `tt-data-league-api-core/src/main/java/org/cttelsamicsterrassa/data/api/core/season_player/`.
   - SeasonPlayerResult: add `modify` and `delete` packages under `tt-data-league-api-core/src/main/java/org/cttelsamicsterrassa/data/api/core/season_player_result/`.
   - Match: add `modify` and `delete` packages under `tt-data-league-api-core/src/main/java/org/cttelsamicsterrassa/data/api/core/match/`.
   - Keep existing `Find*ByIdQuery` handlers where present; only add query handlers if an endpoint lacks a backing query.

3. Implement domain/repository interaction logic in new core handlers with no REST concerns.
   - Follow existing core handler style from `ModifyClubCommandHandler` and `DeleteSeasonPlayerCommandHandler`.
   - For each modify handler: `findById` -> mutate using domain methods -> `save` -> return `DomainCommandResponse.successResponse(...)`; return `failResponse(...)` when not found.
   - For each delete handler: `findById` -> delete by entity/id -> return success/fail response.
   - For Match specifically, replace REST stub behavior by using `FindMatchByIdQuery` from `tt-data-league-api-core/src/main/java/org/cttelsamicsterrassa/data/api/core/match/find/application/FindMatchByIdQuery.java` and add write handlers for modify/delete.

4. Close repository support gaps needed by new handlers (only if missing in contracts).
   - Validate that external repository interfaces used by handlers expose required operations (`findById`, `save`, `delete` or equivalent) for `ClubMember`, `SeasonPlayer`, `SeasonPlayerResult`, `PlayersSingleMatch`.
   - If a required operation is absent, extend/align repository contract via the proper dependency path first (external `tt-data-league-core-domain` / `tt-data-league-core-repository-jpa`) before proceeding with REST wiring.
   - If this repo requires adapter glue, place it in `tt-data-league-api-repository-jpa/src/main/java/org/cttelsamicsterrassa/data/api/repository/jpa/` without introducing unrelated mapping refactors.

5. Complete REST endpoints and mappings in `tt-data-league-api-rest`.
   - `ClubMemberController`: add `GET` by id (backed by `FindClubMemberByIdQuery`), `PUT` modify, `DELETE` by id; keep existing find-by-club/practicioner methods unchanged.
   - `SeasonPlayerController`: add `PUT` modify endpoint only; keep existing create/find/delete/search endpoints unchanged.
   - `SeasonPlayerResultController`: ensure create endpoint remains mapped, then add `GET` by id, `PUT` modify, `DELETE` by id.
   - `MatchController`: replace hardcoded `getMatch` stub with QueryBus call (`FindMatchByIdQuery`), add `PUT` modify and `DELETE` by id with CommandBus.
   - Use existing DTOs where possible (`ClubMemberDto`, `SeasonPlayerDto`, `SeasonPlayerResultDto`, `EnrichedMatchDto`/`MatchDto`); introduce request DTOs only when current DTOs cannot express required update input cleanly.

6. Update REST OpenAPI surface and controller annotations for new/changed endpoints.
   - Add/adjust `@Operation` summaries/descriptions on each new endpoint in the four controllers above.
   - Keep controller-level annotations (`ClubMemberOpenAPIv1Controller`, `SeasonPlayerOpenAPIv1Controller`, `SeasonPlayerResultOpenAPIv1Controller`, `MatchOpenAPIv1Controller`) unchanged unless path correction is required for missing endpoint exposure.
   - Ensure endpoint signatures generate clear request/response schemas and expected status codes.

7. Add core unit tests for all newly introduced handlers.
   - Add tests under `tt-data-league-api-core/src/test/java/...` mirroring package structure (one test class per new handler).
   - Validate success and not-found branches for each modify/delete handler.
   - Follow existing style from `tt-data-league-api-core/src/test/java/org/cttelsamicsterrassa/data/api/core/practicioner/find/FindPracticionerByNameQueryHandlerTest.java` (Mockito + AssertJ, no Spring context).

8. Add REST tests (unit + integration) for endpoint behavior and API docs coverage.
   - Unit tests: add controller tests under `tt-data-league-api-rest/src/test/java/...` using MockMvc standalone pattern (same style as `PracticionerControllerTest`) for each new endpoint and failure branch.
   - Integration tests: extend `tt-data-league-api-rest/src/test/java/org/cttelsamicsterrassa/data/api/rest/OpenApiIntegrationTest.java` to import the four target controllers and assert new FEAT-003 paths are present in `/v3/api-docs`.
   - Keep test scope focused on FEAT-003 endpoints; do not refactor unrelated existing test architecture.

9. Update generated contract and feature documentation.
   - Regenerate and validate `openapi.yaml` through project scripts after REST annotation changes.
   - Create/update `docs/sdd/FEAT-003-DETAILS.md` with this plan and finalized implementation notes.
   - Update FEAT-003 lifecycle in `docs/sdd/FEATURES.md` when implementation starts/completes (`planned` -> `in-progress` -> `done`).

10. Verify feature completion through module-targeted test execution.
   - Core verification: run core tests for new handlers in `tt-data-league-api-core`.
   - REST verification: run REST tests covering controller behavior and OpenAPI integration in `tt-data-league-api-rest`.
   - Accept FEAT-003 only when all new endpoint tests pass and no regressions appear in existing REST/core tests.

# Implementation Guidelines

- Keep FEAT-003 narrowly scoped to the listed REST CRUD gaps; avoid GraphQL and broad architectural refactors.
- Preserve existing CQRS flow: REST controllers only map DTOs and dispatch to `CommandBus`/`QueryBus`; business rules stay in core handlers.
- Reuse existing endpoint naming conventions even if imperfect (`practicioner`, singular resource paths) for backward compatibility.
- Prefer extending current DTOs and mapping utilities; add new DTOs only when required for unambiguous update payloads.
- Standardize failure handling in controllers: map bus failure responses to consistent HTTP error responses already used in module patterns.
- Keep changes module-local and explicit: `rest` for endpoints/docs, `core` for commands/handlers, `repository-jpa` only for missing persistence glue.

# Notes

- Match update contract is the highest design-risk area because current `MatchController#getMatch` is stubbed and `MatchDto` is not clearly aligned with `PlayersSingleMatch`; finalize payload/response contract before coding modify/delete.
- Repository interfaces are provided by external artifacts; if required methods for modify/delete are missing, dependency coordination is a prerequisite and should be tracked as a FEAT-003 blocker.
- `SeasonPlayerResultController` currently lacks explicit HTTP mapping on create; while outside the "missing endpoints" list, endpoint mapping correctness should be preserved when adding findById/modify/delete.
- Existing REST integration coverage is OpenAPI-focused; endpoint behavior confidence will primarily come from new controller unit tests plus targeted integration assertions for exposed paths.

