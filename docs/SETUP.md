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
- Frontend: use a supported Node.js release compatible with the selected Angular version.
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

- Never commit real `.env` files, database credentials, Clerk secret keys, API keys, or private keys.
- Keep local configuration outside source control.
- Provide safe example configuration files when useful.
- Configure each environment using the appropriate environment variables or secret-management mechanism.
- Do not place production secrets in Angular frontend environment files; frontend configuration is visible to users.
- A Clerk publishable key is intended for frontend use. A Clerk secret key must never be included in Angular code or frontend configuration.

### Angular Clerk Configuration

The Angular frontend uses `ngx-clerk` v1.0.0 for Clerk authentication.

1. From the repository root, install the dependencies recorded in the lockfile:

   `npm ci --prefix frontend`

2. Create the local file `frontend/src/environments/environment.ts` with the following contents:

   ```typescript
   export const environment = {
     clerkPublishableKey: 'YOUR_CLERK_PUBLISHABLE_KEY',
   } as const;
   ```

3. Obtain the publishable key from the project's Clerk application dashboard. Use the appropriate development key for local development.
4. Replace `YOUR_CLERK_PUBLISHABLE_KEY` with the actual publishable key on your own machine.
5. Keep this file local. It is excluded by the repository's `.gitignore` and must not be committed.
6. Never put a Clerk secret key (`sk_test_...` or `sk_live_...`) in Angular code or frontend configuration.
7. Start the frontend from the repository root:

   `npm --prefix frontend start`

8. Open `http://localhost:4200/` in a browser.

The application uses the following authentication routes:

- `/sign-in` — Clerk sign-in.
- `/sign-up` — Clerk sign-up.
- `/home` — signed-in home page protected by a Clerk route guard.
- `/` — redirects to `/home`; Clerk redirects signed-out users to sign-in.

The configured Clerk application must allow the local development origin and use the authentication methods agreed for Enishi.

Because the environment file is local-only, each developer must configure their own publishable key. Do not share or commit a developer's local environment file.

## 5. Authentication and Database Setup

Clerk provides hosted sign-up/sign-in, session management, and token issuance.

The Angular integration uses `ClerkService.getToken()` to retrieve the active Clerk session token when available. Token retrieval has been manually verified in the signed-in frontend.

For future protected API requests:

1. Retrieve the appropriate Clerk-issued session token.
2. Send it in the HTTP `Authorization: Bearer <token>` header.
3. Handle missing or expired sessions appropriately in the frontend.
4. Never log or expose the token unnecessarily.

Token retrieval does not establish that the backend accepts the token. Backend authentication must be implemented independently.

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

For the Angular frontend, run these commands from the repository root:

- Production build: `npm --prefix frontend run build`
- Tests: `npm --prefix frontend test -- --watch=false`

## 7. Local AI Instructions

`AGENTS.md` is local-only and ignored by Git. Shared technical decisions and project instructions must be documented in tracked repository files so every collaborator has the same source of truth.
