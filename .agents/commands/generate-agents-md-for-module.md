# Prompt: Generate AGENTS.md for a Maven module

## Task

Analyze the Maven module located at `{{MODULE_PATH}}` and generate a comprehensive
`AGENTS.md` file tailored for AI coding agents. The file should give any agent
enough context to work confidently inside this module without needing to read
every source file.

## Instructions

Read the following before writing anything:
- `{{MODULE_PATH}}/pom.xml` — dependencies, plugins, parent POM reference
- `{{MODULE_PATH}}/src/main/java/` — package structure and key classes
- `{{MODULE_PATH}}/src/main/resources/` — config files, properties, application.yml
- `{{MODULE_PATH}}/src/test/java/` — testing patterns and frameworks used
- The root `AGENTS.md` if it exists — inherit global conventions, do not repeat them

---

## Output format

Write the `AGENTS.md` using the following sections. Omit any section that is
genuinely not applicable, but include a one-line note explaining why.

---

### 1. Module purpose
- One short paragraph: what this module does and why it exists.
- What problem does it solve in the broader system?
- Is it a library, a service, a shared API contract, a batch job, a gateway?

### 2. Architecture overview
- Describe the internal layering (e.g. controller → service → repository).
- List the main design patterns in use (e.g. CQRS, event-driven, hexagonal).
- Note any framework-specific conventions (Spring Boot, Quarkus, Micronaut, etc.).

### 3. Entry points
List the most important classes/interfaces an agent should know:
- Public API surface (REST controllers, gRPC services, Kafka listeners, etc.)
- Core domain/service classes
- Configuration beans
- Scheduled jobs or event handlers

Format as: `FullyQualifiedClassName` — one-line description.

### 4. Module dependencies
- Other modules in this project this module depends on (from `<dependencies>` in pom.xml).
- Key external libraries and what they are used for.
- Any BOM or parent POM constraints to be aware of.

### 5. Build & test commands
Provide exact commands an agent can run:
```bash
# Build this module only
mvn -pl {{MODULE_PATH}} clean install -DskipTests

# Run all tests
mvn -pl {{MODULE_PATH}} test

# Run a specific test class
mvn -pl {{MODULE_PATH}} -Dtest=MyServiceTest test

# Run with a specific profile (if applicable)
mvn -pl {{MODULE_PATH}} test -P integration-tests
```
Note any test containers, required environment variables, or external services
needed to run tests successfully.

### 6. Configuration
- List all environment variables or system properties the module reads.
- List property files and which Spring profile (or equivalent) they belong to.
- Describe feature flags if present.
- Highlight any secrets and where they are expected to come from (Vault, env, etc.).

### 7. Coding conventions (module-specific)
Only include rules that differ from or extend the root-level `AGENTS.md`:
- Naming conventions for this module's domain objects.
- Preferred exception handling patterns.
- Required annotations or markers (e.g. `@Transactional` scope rules).
- Code generation tools in use (MapStruct, Lombok, jOOQ, etc.) and how to work with them.
- Any files that are auto-generated and must NOT be edited by hand — mark them clearly.

### 8. Constraints and fragile areas
Be explicit about what agents should avoid or treat with extra care:
- Files that are auto-generated (list them).
- Areas with known technical debt that require human review.
- Concurrency-sensitive sections.
- External contracts (API versioning, DB schema owned by another team, etc.).
- Any classes or packages that must not be modified without a specific process.

### 9. Testing strategy
- Describe the testing pyramid for this module (unit / integration / contract / e2e).
- How are external dependencies mocked? (Mockito, WireMock, Testcontainers?)
- Where is test data set up? (SQL scripts, fixtures, factories?)
- Are there any flaky tests the agent should be aware of?

### 10. Related context
- Link to the root `AGENTS.md` if conventions are inherited.
- Link to OpenAPI / AsyncAPI specs if this module exposes or consumes an API.
- Link to ADRs (Architecture Decision Records) relevant to this module.
- Link to runbooks, Confluence pages, or other docs.

---

## Quality checklist before saving

Before writing the file, verify:
- [ ] Every class name mentioned actually exists in `src/main/java/`.
- [ ] Every command has been checked against the actual `pom.xml` (correct module name, correct profiles).
- [ ] No section copies boilerplate from the root `AGENTS.md` verbatim — only additions and overrides.
- [ ] The file is useful to an agent that has NEVER seen this codebase before.
- [ ] The file is concise — prefer bullet points over paragraphs, and code blocks over prose for commands.

## Output

Write the file to `{{MODULE_PATH}}/AGENTS.md`.
Also suggest whether a root-level `AGENTS.md` should be created or updated
based on patterns you observed that are shared across modules.
