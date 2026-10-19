# Enishi — Architecture Decisions

This document records the project's current decisions. New decisions should be added here when they materially affect architecture, dependencies, security, or deployment.

## 1. Confirmed Decisions

| Area | Decision | Reason / constraint |
|---|---|---|
| Product name | Enishi | Final project name |
| Repository | `cbsinha/enishi` | Public GitHub repository |
| Architecture | Incremental microservices | Selected as a learning and interview objective |
| Frontend | Angular + TypeScript | Primary web frontend |
| Backend | Java 25 + Spring Boot | Backend implementation target |
| Build tooling | Maven Wrapper | Reproducible project builds |
| Authentication | Clerk | Hosted sign-up/sign-in, sessions, and token issuance |
| API authentication | Spring Security OAuth2 Resource Server | Validate Clerk-issued JWTs |
| Database | Neon PostgreSQL | Hosted persistent database |
| Database migrations | Flyway or equivalent | Controlled schema changes |
| Collaboration | CLI-based Git, pull requests, WhatsApp | Agreed team workflow |
| IDE | IntelliJ IDEA | Primary development environment |
| Local AI instructions | `AGENTS.md` | Local-only; ignored by Git |
| Shared technical context | Tracked documentation under `docs/` | Shared source of truth for project decisions |

## 2. Security Decisions

- Never commit secrets, access tokens, database passwords, or private keys.
- Authentication and authorization are separate responsibilities.
- Every service must enforce its own resource-level authorization.
- Use the verified Clerk identity to associate requests with Enishi user records.
- Do not create a custom JWT issuer or use JJWT solely to validate Clerk-issued tokens.
- Never rely on frontend checks as the only access control.
- AI-generated changes must be validated and presented for user approval where appropriate.

## 3. Architecture Decisions

- Start with the Trip Service and a working end-to-end journey.
- Add the Collaboration, Discovery, and Itinerary services in their respective development phases.
- Keep service boundaries and API contracts explicit.
- A shared Neon project may be used early, but logical data ownership must remain clear.
- Redis and Kafka are not initial requirements; add them only if justified by measured needs.
- Live video hangouts are a later-stage feature.

## 4. Decisions Intentionally Deferred

| Decision | When to revisit |
|---|---|
| Angular Clerk integration package and exact version | Before frontend authentication implementation |
| Clerk JWT issuer, claims, and JWKS configuration | Before protecting backend endpoints |
| Neon environment separation and database roles | Before establishing persistent application data |
| Maps, places, and routing provider | Before Discovery implementation |
| AWS compute and static hosting services | Before deployment |
| Poll rules and voting behavior | Before Collaboration implementation |
| Itinerary feasibility rules | Before Itinerary implementation |
| Profile visibility defaults | Before Travel Profile implementation |
| AI provider, model, budget, and evaluation | Before AI planning implementation |
| Redis or Kafka | When a concrete performance or delivery requirement appears |
| LiveKit configuration and usage limits | Before Live Hangouts implementation |
| CI/CD, monitoring, backup, and recovery strategy | During reliability and deployment work |

## 5. Changing a Decision

When a decision changes:
1. Update this document.
2. Update `docs/ARCHITECTURE.md`, `docs/API_CONTRACTS.md`, or `docs/SETUP.md` if affected.
3. Explain the reason and relevant trade-offs.
4. Communicate the change to collaborators before implementing dependent work.

The latest documented decision takes precedence over outdated notes or assumptions.
