# FEATURES.md — Feature Registry & Build Plans

This file is the single source of truth for planned, in-progress, and completed features.

**For humans:** Add new features under `## Backlog` using the template below.
**For agents:** Only work on features marked `status: ready`. Update status as you progress. Never modify features marked `status: done` or `status: in-progress` unless explicitly asked.

---

## Status Legend

| Status | Meaning |
|-|-|
| `idea` | Captured but not planned yet — no build plan written |
| `planned` | Build plan written, not yet ready to implement |
| `ready` | Build plan approved, agent can start |
| `in-progress` | Currently being implemented |
| `done` | Shipped |
| `blocked` | Waiting on a dependency or decision |

---

## Template

Copy this block to add a new feature:

```
### [FEAT-000] Feature Name
- **Status:** idea
- **Priority:** low | medium | high
- **Effort:** small (< 2h) | medium (2–8h) | large (> 8h)
- **Depends on:** —

#### Goal
One sentence: what problem does this solve for the user?

#### Acceptance Criteria
- [ ] Criterion 1
- [ ] Criterion 2

#### Feature Details
→ See [FEAT-000-DETAILS.md](./FEAT-000-DETAILS.md) for a detailed breakdown of the feature, build plan, and implementation steps.
```

### Feature Details file format
```
# Build Plan
> Fill this in when status moves to `planned`.

1. Step 1
2. Step 2
...

# Implementation Guidelines

# Notes
Any open questions, design decisions, or links.
```

## In Progress

## Backlog

### [FEAT-004] Adapt for Role and Permisions for User Management
- **Status:** idea
- **Priority:** high
- **Effort:** medium
- **Depends on:** —

#### Goal
Adapt to dependencies for User management and repositories and Authentication to support Role and Permissions.
This will allow for more granular access control and better security in the application.

#### Acceptance Criteria
- [ ] User management supports roles and permissions.
- [ ] Authentication system is updated to handle role-based access control.
- [ ] All changes are properly tested and documented.

#### Feature Details
→ See [FEAT-004-DETAILS.md](./FEAT-004-DETAILS.md) for a detailed breakdown of the feature, build plan, and implementation steps.

## Done

---

### [FEAT-003] Complete missing endpoints for entities covering CRUD operations
- **Status:** done
- **Priority:** high
- **Effort:** large
- **Depends on:** —

#### Goal
Implement missing REST endpoints for all entities to cover Create, Read, Update, and Delete (CRUD) operations. This will ensure that the API is fully functional and allows for complete management of all entities.
- ClubMember entity: findById, modify, delete.
- SeasonPlayer entity: modify.
- SeasonPlayerResult entity: findById, modify, delete.
- Match entity: findById, modify, delete.
Complete any missing components, such as services and repositories, to support these endpoints.
- Ensure that all new endpoints are properly tested with unit and integration tests.
- Update documentation to reflect the new endpoints and their usage.

#### Acceptance Criteria
- [x] All missing CRUD endpoints for the specified entities are implemented.
- [x] All new endpoints are fully functional and tested.
- [x] Documentation is updated to include the new endpoints.
- [x] No existing functionality is broken by the new endpoints.

#### Feature Details
→ See [FEAT-003-DETAILS.md](./FEAT-003-DETAILS.md) for a detailed breakdown of the feature, build plan, and implementation steps.

---

### [FEAT-002] New rest endpoint for Practicioners `/practicioner/find_by_similar_name=name=<fragment name>` find by fullName fragment (case-insensitive, partial match)
- **Status:** done
- **Priority:** medium
- **Effort:** medium
- **Depends on:** —

#### Goal
Add a new REST endpoint to query practitioners by a fragment of their full name, allowing for case-insensitive and partial matches. This will enhance the search capabilities for practitioners in the system.

#### Acceptance Criteria
- [x] A new REST endpoint is available at `/practicioner/find_by_similar_name`.
- [x] The endpoint accepts a query parameter `name` which is used to search for practitioners.
- [x] The search is case-insensitive and supports partial matches (e.g., searching for "smith" should match "John Smith" and "Smithers").
- [x] The endpoint returns a list of practitioners that match the search criteria.
- [x] Appropriate error handling is in place for invalid input (e.g., missing `name` parameter).
- [x] Unit and integration tests cover the new endpoint and its functionality.
- [x] Documentation is updated to include the new endpoint and its usage.

#### Feature Details
→ See [FEAT-002-DETAILS.md](./FEAT-002-DETAILS.md) for a detailed breakdown of the feature, build plan, and implementation steps.


---

### [FEAT-001] Reafactor AuthController to Rest Module
- **Status:** done
- **Priority:** high
- **Effort:** medium
- **Depends on:** —

#### Goal
Refactor: Move AuthController from runtime module to rest module to decouple authentication logic from runtime and improve modularity.

#### Acceptance Criteria
- [x] AuthController is successfully moved to the rest module.
- [x] All authentication functionalities remain intact and work as expected after the move.
- [x] No runtime module dependencies on AuthController remain after the move.

#### Feature Details
→ See [FEAT-001-DETAILS.md](./FEAT-001-DETAILS.md) for a detailed breakdown of the feature, build plan, and implementation steps.

---
