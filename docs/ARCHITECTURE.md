# Enishi — Architecture

## 1. Architectural Direction

Enishi uses an **incremental microservices architecture**. Services are introduced as their corresponding features are developed and tested. Avoid creating every planned service before the first end-to-end user journey works.

Each service should have a clear responsibility, API boundary, authorization rules, and data ownership.

## 2. High-Level Components

### Frontend
- Angular with TypeScript.
- Communicates with backend services through HTTP APIs.
- Uses RxJS and Angular Signals where appropriate.
- Integrates with Clerk for hosted authentication and session management.
- Uses `ngx-clerk` v1.0.0, an unofficial community-maintained Angular SDK.
- Uses Clerk's Angular integration to track session state, protect frontend routes, and retrieve session tokens.

### Trip Service — Phase 1
Responsible for:
- Creating and managing trips.
- Managing trip membership.
- Handling invitations.
- Enforcing trip-level roles and permissions.

### Collaboration Service — Phase 2
Planned responsibilities:
- Persistent group chat.
- Mentions and collaboration events.
- Destination proposals, polls, and votes.
- Real-time updates through WebSockets.

### Discovery Service — Phase 3
Planned responsibilities:
- External place search integration.
- Attraction discovery and saved-place management.
- Provider-backed place metadata.

### Itinerary Service — Phase 4
Planned responsibilities:
- Day-by-day itinerary items.
- Ordering activities.
- Route estimates and feasibility checks.

### Later Services
Travel profiles, AI-assisted planning, notifications, and live hangouts will be introduced when their roadmap phases begin.

## 3. Authentication and API Security

Clerk handles hosted sign-up/sign-in, sessions, and token issuance.

The Angular frontend uses `ngx-clerk` v1.0.0. The integration's `ClerkService.getToken()` method has been manually verified to return a session token when a user is signed in.

For protected backend APIs:

1. Angular obtains an appropriate Clerk-issued token.
2. The frontend sends the token in the HTTP `Authorization: Bearer <token>` header.
3. Spring Security OAuth2 Resource Server validates the JWT using the configured issuer and signing-key/JWKS settings.
4. Service-level logic enforces authorization for the requested resource and action.

The frontend has verified token retrieval. A protected request to an Enishi backend endpoint has not yet been verified as part of this frontend integration work.

Do not create a custom JWT issuer or add JJWT solely to validate Clerk tokens.

Authentication establishes identity; it does not automatically grant access to a trip or its data.

Frontend route guards improve navigation and user experience but must never be treated as the backend's security boundary.

## 4. User Identity Mapping

Enishi will maintain an application-level user record associated with the stable Clerk user ID.

Use the verified token subject as the identity reference after confirming the configured Clerk token claims. Do not trust a client-supplied user ID as proof of identity.

## 5. Authorization Responsibilities

Every protected operation must check the relevant permissions server-side, including:
- Trip membership and membership status.
- Trip-level role requirements.
- Resource ownership where applicable.
- Permission to perform the requested action.
- Visibility rules for profiles and travel history.

Services must not assume that successful authentication implies permission to access a resource.

## 6. Database and Persistence

- Database: Neon PostgreSQL.
- No local database server is required as the persistent store.
- Database migrations will use Flyway or an equivalent migration tool.
- The initial deployment may use a shared Neon project, but logical data ownership must remain clear.
- Database credentials must be stored outside source control.

Service boundaries should remain meaningful even while infrastructure is shared during early development.

## 7. External Integrations

Planned integrations include Clerk, Neon, a future maps/places/routing provider, an AI provider, and a video provider for later live hangouts.

The exact AWS hosting services and external providers remain decisions for their relevant phases.

Redis and Kafka are optional. Add them only when a measured requirement justifies their operational complexity.

## 8. Reliability and Engineering Practices

- Keep controllers thin and business rules in appropriate service/domain layers.
- Use explicit API contracts and DTOs.
- Validate incoming requests.
- Apply database constraints and migrations.
- Test authorization, including cross-trip access attempts.
- Handle network failures and external-provider errors deliberately.
- Avoid logging secrets, access tokens, or unnecessary personal data.

## 9. Architecture Principles

1. Build one working end-to-end journey before expanding the system.
2. Keep service responsibilities and data ownership explicit.
3. Enforce authorization in the backend.
4. Keep external integrations behind clear boundaries.
5. Add infrastructure only when justified.
6. Keep shared architecture decisions in tracked documentation.
