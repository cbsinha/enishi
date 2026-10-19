# Enishi — Development Setup

## 1. Prerequisites

Install and verify the following tools:

- Java Development Kit (JDK) 25.
- Node.js version compatible with the selected Angular release.
- npm, installed with Node.js.
- Git for Windows.
- IntelliJ IDEA.

Maven will be managed through the Maven Wrapper generated with the Spring Boot project. A global Maven installation is not required.

Docker and a local PostgreSQL server are not initial prerequisites. Neon PostgreSQL will provide the hosted database when database integration begins.

## 2. Team Version Alignment

The team should agree on compatible tool versions before generating application projects.

- Backend: Java 25.
- Frontend: use a supported Node.js LTS release compatible with the selected Angular version.
- Angular: pin project dependencies and commit the lockfile.
- Java dependencies: use the Maven Wrapper and commit its required wrapper files.
- Git: use the repository's `main` branch as the default branch.

Check the output of `java -version`, `javac -version`, `node -v`, `npm -v`, and `git --version` when diagnosing setup issues.

## 3. Repository Workflow

1. Clone the repository.
2. Create a focused feature branch from the latest `main`.
3. Implement and test one coherent change.
4. Commit with a descriptive message.
5. Push the branch and open a pull request.
6. Review and merge the pull request into `main`.

Avoid making unrelated changes in a single pull request. Keep `main` stable and protect it through GitHub branch rules when configured.

## 4. Configuration and Secrets

- Never commit real `.env` files, database credentials, Clerk secrets, API keys, or private keys.
- Keep local configuration outside source control.
- Provide safe example configuration files when useful.
- Configure each environment using the appropriate environment variables or secret-management mechanism.
- Do not place production secrets in Angular frontend environment files; frontend configuration is visible to users.

## 5. Authentication and Database Setup

Clerk and Neon integration will be configured during the first implementation phase.

Before protecting backend endpoints, verify the Clerk token issuer, expected claims, and signing-key/JWKS configuration. Use Spring Security OAuth2 Resource Server to validate Clerk-issued tokens.

Use database migrations to manage schema changes. Do not rely on manual production schema edits.

## 6. Testing and Build Verification

Each service should document its build and test commands once its project has been generated.

Before submitting a pull request:
- Run the relevant build.
- Run automated tests.
- Review the changes with `git diff`.
- Confirm that no secrets or generated build files are included.
- Update shared documentation when contracts or architectural decisions change.

## 7. Local AI Instructions

`AGENTS.md` is local-only and ignored by Git. Shared technical decisions and project instructions must be documented in tracked repository files so every collaborator has the same source of truth.
