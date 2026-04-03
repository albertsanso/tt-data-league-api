# tt-data-league-api

A **Spring Boot** API platform for managing table tennis league data. This project exposes both **REST** and **GraphQL** interfaces over a core domain model, implementing a layered architecture with **CQRS** (Command Query Responsibility Segregation) patterns. The system manages clubs, practitioners, players, matches, and season data for competitive table tennis leagues.

---

## Table of Contents

- [Architecture overview](#architecture-overview)
- [Technology stack](#technology-stack)
- [Repository layout](#repository-layout)
- [Prerequisites](#prerequisites)
- [Getting started](#getting-started)
  - [1. Start the database](#1-start-the-database)
  - [2. Build the project](#2-build-the-project)
  - [3. Run the application](#3-run-the-application)
- [Configuration](#configuration)
- [Deployment](#deployment)
- [API access](#api-access)
  - [REST API](#rest-api)
  - [GraphQL API](#graphql-api)
- [Build & test commands](#build--test-commands)
- [OpenAPI contract](#openapi-contract)
- [Module descriptions](#module-descriptions)
- [Domain glossary](#domain-glossary)
- [Contributing](#contributing)
- [License](#license)

---

## Architecture overview

```
┌──────────────────────────────────────────────────────┐
│                  tt-data-league-api-runtime           │  ← Spring Boot entry point
│                       (runnable JAR)                  │
├──────────────────┬───────────────────────────────────┤
│  tt-data-league- │  tt-data-league-api-graphql        │  ← GraphQL resolvers & schema
│  api-rest        │                                    │  ← REST controllers & DTOs
│  (controllers,   │  (Spring GraphQL / graphql-java)   │
│   springdoc,JWT) │                                    │
├──────────────────┴───────────────────────────────────┤
│              tt-data-league-api-core                  │  ← CQRS command/query handlers
├───────────────────────────────────────────────────────┤
│          tt-data-league-api-repository-jpa            │  ← JPA repository implementations
├───────────────────────────────────────────────────────┤
│  External: tt-data-league-core-domain                 │  ← Domain entities & contracts
│  External: tt-data-league-core-repository-jpa         │  ← Base repository interfaces
└───────────────────────────────────────────────────────┘
```

The system uses synchronous, in-memory buses from the Albert Sanso libraries:

| Bus | Purpose |
|---|---|
| **CommandBus** | Dispatches write operations to command handlers |
| **QueryBus** | Dispatches read operations to query handlers |
| **EventBus** | Publishes domain events to event subscribers |

---

## Technology stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.8 |
| REST | Spring MVC + Spring Security 6.5.5 |
| GraphQL | Spring GraphQL 1.4.1 + graphql-java 21.0 |
| Persistence | Spring Data JPA, Hibernate, HikariCP |
| API docs | springdoc-openapi 2.0.4 + Swagger UI |
| Security | Spring Security + JJWT 0.12.5 (JWT tokens) |
| Serialization | Jackson 2.19.4 |
| Code generation | Lombok 1.18.42 |
| Database (primary) | PostgreSQL 42.7.8 |
| Database (fallback) | MySQL 9.4.0 |
| Build tool | Maven |

---

## Repository layout

```
tt-data-league-api/
├── tt-data-league-api-runtime/        Spring Boot main service (runnable JAR)
├── tt-data-league-api-rest/           REST controllers, DTOs, OpenAPI config
├── tt-data-league-api-graphql/        GraphQL resolvers and schema
├── tt-data-league-api-core/           Business logic: command/query handlers
├── tt-data-league-api-repository-jpa/ JPA repository implementations
├── docker/                            Docker Compose files for local dev
├── docs/                              Developer documentation
├── scripts/                           OpenAPI helper scripts (Python)
├── .github/                           GitHub Actions workflows
└── openapi.yaml                       OpenAPI 3 contract (generated)
```

---

## Prerequisites

- **Java 21+** — required to compile and run the application.
- **Maven** — `mvn` available in your shell `PATH`.
- **Docker & Docker Compose** — for spinning up a local PostgreSQL instance.
- **Python 3** _(optional)_ — for running the OpenAPI scripts in `scripts/`.
- **Node.js / npm** _(optional)_ — to run `swagger-cli validate` for OpenAPI validation.
- External Maven artifacts must be installed to your local repository beforehand:
  - `tt-data-league-core-domain`
  - `tt-data-league-core-repository-jpa`
  - Albert Sanso bus libraries (`commons-core`, `commandbus-synchronous-inmemory`, `querybus-synchronous-inmemory`, `eventbus-synchronous-inmemory`)

If `mvn` is not recognized:

```powershell
winget install Apache.Maven
mvn -v
```

Alternative (Chocolatey):

```powershell
choco install maven -y
mvn -v
```

Inside WSL Debian:

```bash
sudo apt update
sudo apt install -y maven
mvn -v
```

---

## Getting started

### 1. Start the database

Use the provided Docker Compose file to start a local PostgreSQL instance:

```bash
docker compose -f docker/docker-compose.yml up -d
```

This starts PostgreSQL on `localhost:5432` with:

| Setting | Value |
|---|---|
| Database | `mydb` |
| Username | `compose-postgres` |
| Password | `compose-postgres` |

> **Note:** The runtime `application.yaml` defaults to `localhost:15432` with user `ttleagueuser`. Override the datasource settings via environment variables when using the Docker container:
>
> ```bash
> export DB_TTLEAGUEDATA_JDBC_URL=jdbc:postgresql://localhost:5432/mydb
> export DB_TTLEAGUEDATA_CREDENTIAL_USERNAME=compose-postgres
> export DB_TTLEAGUEDATA_CREDENTIAL_PASSWORD=compose-postgres
> ```
>
> On Windows PowerShell:
>
> ```powershell
> $env:DB_TTLEAGUEDATA_JDBC_URL = "jdbc:postgresql://localhost:5432/mydb"
> $env:DB_TTLEAGUEDATA_CREDENTIAL_USERNAME = "compose-postgres"
> $env:DB_TTLEAGUEDATA_CREDENTIAL_PASSWORD = "compose-postgres"
> ```

### 2. Build the project

```bash
# Build all modules (skip tests for a faster first build)
mvn clean install -DskipTests

# Build and run all tests
mvn clean install
```

On Windows:

```powershell
mvn clean install -DskipTests
```

### 3. Run the application

```bash
java -jar tt-data-league-api-runtime/target/tt-data-league-api-runtime-*.jar
```

The application starts on port **8080** by default. The management/actuator port is **9091**.

#### Environment variables

Override defaults at startup using these environment variables:

| Variable | Default | Description |
|---|---|---|
| `DB_TTLEAGUEDATA_JDBC_URL` | `jdbc:postgresql://localhost:15432/ttleaguedata` | Database connection URL |
| `DB_TTLEAGUEDATA_CREDENTIAL_USERNAME` | `ttleagueuser` | Database username |
| `DB_TTLEAGUEDATA_CREDENTIAL_PASSWORD` | `ttleaguepass` | Database password |

#### Run the application from commandline with environment variables:

```bash
DB_TTLEAGUEDATA_JDBC_URL=jdbc:postgresql://localhost:15432/ttleaguedata \
DB_TTLEAGUEDATA_CREDENTIAL_USERNAME=ttleagueuser \
DB_TTLEAGUEDATA_CREDENTIAL_PASSWORD=ttleaguepass \
java -jar tt-data-league-api-runtime/target/tt-data-league-api-runtime-*.jar
``` 
---

## Configuration

Runtime configuration is located in:

```
tt-data-league-api-runtime/src/main/resources/application.yaml
```

Key settings:

| YAML key | Default | Description |
|---|---|---|
| `spring.datasource.url` | `${DB_TTLEAGUEDATA_JDBC_URL:jdbc:postgresql://localhost:15432/ttleaguedata}` | Database URL |
| `spring.datasource.username` | `${DB_TTLEAGUEDATA_CREDENTIAL_USERNAME:ttleagueuser}` | Database username |
| `spring.datasource.password` | `${DB_TTLEAGUEDATA_CREDENTIAL_PASSWORD:ttleaguepass}` | Database password |
| `spring.datasource.driver-class-name` | `org.postgresql.Driver` | JDBC driver |
| `spring.datasource.hikari.maximum-pool-size` | `10` | Max HikariCP connections |
| `spring.datasource.hikari.minimum-idle` | `5` | Min idle connections |
| `spring.datasource.hikari.connection-timeout` | `20000` | Connection timeout (ms) |
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema strategy (use Flyway/Liquibase for production) |
| `spring.jpa.show-sql` | `false` | Log SQL statements |
| `management.server.port` | `9091` | Actuator/management port |
| `management.endpoint.health.show-details` | `always` | Health endpoint detail level |

All datasource values are resolved from environment variables with fallback defaults (Spring Boot `${VAR:default}` syntax). Set the variables listed under [Environment variables](#environment-variables) to override them at runtime.

---

## Deployment

This repository includes deployment scripts for a **local WSL Debian** instance, following the same "build JAR + run as a Linux service" model described in `docs/springboot-deploy-and-run-as-jar.md`.

### What the deployment scripts do

1. Build the runtime JAR (`tt-data-league-api-runtime`) on Windows (unless you skip build).
2. Copy the generated JAR into WSL.
3. Create/update a `systemd` service in WSL.
4. Inject database environment variables from `application.yaml` placeholders.
5. Enable and restart the service.

Scripts:

- `scripts/wsl-local-deployment/deploy-to-wsl-debian.ps1` (entry point from Windows PowerShell)
- `scripts/wsl-local-deployment/wsl/deploy_service.sh` (runs inside WSL Debian)

### Prerequisites (WSL Debian)

- `wsl.exe` installed and a Debian distro available.
- `systemd` enabled in that distro.
- Java 21 installed inside WSL Debian.
- A reachable PostgreSQL instance (or override DB variables).

For WSL setup details, see `docs/wsl/wsl-debian-setup.md`.

### Deploy from Windows PowerShell

Run from the repository root:

```powershell
.\scripts\wsl-local-deployment\deploy-to-wsl-debian.ps1
```

Example with explicit overrides:

```powershell
.\scripts\wsl-local-deployment\deploy-to-wsl-debian.ps1 `
  -Distro Debian `
  -LinuxUser asanso `
  -ServiceName tt-data-league-api `
  -InstallDir /opt/tt-data-league-api `
  -LogFile /var/log/tt-data-league-api/app.log `
  -DbUrl "jdbc:postgresql://localhost:5432/ttleaguedata" `
  -DbUsername ttleagueuser `
  -DbPassword ttleaguepass
```

### Script parameters

| Parameter | Default | Description |
|---|---|---|
| `-Distro` | `Debian` | WSL distro name (`wsl --list --quiet`) |
| `-LinuxUser` | `asanso` | Linux account that runs the service |
| `-InstallDir` | `/opt/tt-data-league-api` | Target directory in WSL |
| `-ServiceName` | `tt-data-league-api` | Name of the systemd service |
| `-DbUrl` | `jdbc:postgresql://localhost:15432/ttleaguedata` | `DB_TTLEAGUEDATA_JDBC_URL` |
| `-DbUsername` | `ttleagueuser` | `DB_TTLEAGUEDATA_CREDENTIAL_USERNAME` |
| `-DbPassword` | `ttleaguepass` | `DB_TTLEAGUEDATA_CREDENTIAL_PASSWORD` |
| `-LogFile` | _(empty)_ | Optional service stdout/stderr file (passed to `--log-file`; default handled in Linux script) |
| `-SkipBuild` | `false` | Skip Maven package phase and deploy latest existing JAR |

### Operate the deployed service

```powershell
wsl -d Debian -- systemctl status tt-data-league-api --no-pager
wsl -d Debian -- sudo journalctl -u tt-data-league-api -n 100 --no-pager
wsl -d Debian -- sudo systemctl restart tt-data-league-api
wsl -d Debian -- sudo systemctl stop tt-data-league-api
```

If you only change code, redeploy by running `scripts/wsl-local-deployment/deploy-to-wsl-debian.ps1` again.

---

## API access

### REST API

Once the application is running, access the interactive documentation at:

| Resource | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| OpenAPI YAML | http://localhost:8080/v3/api-docs.yaml |

Available REST resource groups:

- **Clubs** — create, read, update, and delete club entities.
- **Practitioners** — manage table tennis practitioners.
- **Club Members** — link practitioners to clubs across seasons.
- **Season Players** — players registered for a specific season.
- **Season Player Results** — match results per season player.
- **Matches** — detailed match data with competition info.

Authentication uses **JWT bearer tokens**. Obtain a token via the authentication endpoint and pass it as `Authorization: Bearer <token>` on protected routes.

### GraphQL API

GraphQL endpoint: `http://localhost:8080/graphql`

GraphiQL playground (if enabled): `http://localhost:8080/graphiql`

Example queries:

```graphql
# List all clubs
query {
  listAllClubs {
    id
    name
    yearRanges
  }
}

# Find matches by season, competition and practitioner
query {
  findMatchesBySeasonAndCompetitionAndMatchDayAndPracticionerName(
    season: "2024-2025"
    competitionInfo: { competitionType: "LEAGUE" }
    practitionerName: "Smith"
  ) {
    id
    matchDateTime
    localPlayerName
    visitorPlayerName
  }
}
```

See `tt-data-league-api-graphql/src/main/resources/graphql/schema.graphqls` for the full schema.

---

## Build & test commands

```bash
# Build entire project (all modules)
mvn clean install -DskipTests

# Build and run all tests
mvn clean install

# Build a single module and its dependencies
mvn -pl tt-data-league-api-<module-name> -am clean install

# Run all tests
mvn test

# Run tests for a specific module
mvn -pl tt-data-league-api-rest test

# Quick compile check (no tests, no install)
mvn clean compile

# Check for dependency updates
mvn versions:display-dependency-updates
```

---

## OpenAPI contract

The `openapi.yaml` file at the project root is the canonical OpenAPI 3 contract. It is **generated** — do not edit it directly.

```bash
# Regenerate the contract from source annotations
python scripts/regenerate_openapi.py

# Quick structural check
python scripts/verify_openapi.py

# Validate with swagger-cli
swagger-cli validate openapi.yaml
```

The GitHub Actions workflow at `.github/workflows/validate-openapi.yaml` validates this file on every pull request.

---

## Module descriptions

| Module | Description |
|---|---|
| `tt-data-league-api-runtime` | Spring Boot entry point; wires all modules together and produces the runnable JAR. |
| `tt-data-league-api-rest` | REST controllers, request/response DTOs, JWT security filter, Swagger/OpenAPI config. |
| `tt-data-league-api-graphql` | GraphQL resolvers, schema definition (`schema.graphqls`), and query wiring. |
| `tt-data-league-api-core` | CQRS command handlers, query handlers, and application service logic. |
| `tt-data-league-api-repository-jpa` | Spring Data JPA repository implementations; bridges core domain repositories to the database. |

---

## Domain glossary

| Term | Definition |
|---|---|
| **Club** | A table tennis club entity with members across multiple seasons. |
| **Practitioner** | A table tennis player (practitioner of the sport). |
| **Season** | A time period (year range) during which matches occur, e.g. `2024-2025`. |
| **Match** | A game result between players/teams with detailed statistics. |
| **SeasonPlayer** | A player registered for a particular season, with results linked. |
| **ClubMember** | A practitioner linked to a club for one or more year ranges. |
| **CQRS** | Command Query Responsibility Segregation — separates write (Commands) and read (Queries) models. |

---

## Contributing

1. Fork the repository and create a feature branch from `main`.
2. Ensure `mvn clean install` passes (build + tests).
3. Validate the OpenAPI contract if REST endpoints were changed: `swagger-cli validate openapi.yaml`.
4. Submit a pull request; the CI pipeline will run OpenAPI validation automatically.

> For AI coding agents working in this repository, refer to [AGENTS.md](./AGENTS.md) for project conventions and constraints.

---

## License

This project is licensed under the terms found in the [LICENSE](./LICENSE) file.

