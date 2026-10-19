# Enishi

**Collaborative travel planning · Real-time groups · AI-assisted itineraries**

Enishi is a collaborative travel-planning web application designed to help groups of friends plan trips together. It combines group collaboration, destination decisions, attraction discovery, shared itineraries, and AI-assisted planning in one place.

The goal is to help groups make better travel decisions without taking control away from users.

## Planned Features

- Create trips, invite friends, and manage group membership.
- Collaborate through persistent group chat and destination proposals.
- Vote on destinations and attractions.
- Discover places and build shared attraction shortlists.
- Create and edit day-by-day itineraries.
- Maintain user-controlled travel profiles and explicitly recorded visits.
- Use AI-assisted planning to propose changes that users can review and approve.
- Add independent live video hangouts in a later development phase.

## Technology Stack

| Area | Technology |
|---|---|
| Frontend | Angular and TypeScript |
| Backend | Java 25 and Spring Boot microservices |
| Authentication | Clerk |
| API security | Spring Security OAuth2 Resource Server with JWT validation |
| Database | Neon PostgreSQL |
| Database migrations | Flyway or an equivalent migration tool |
| Version control | Git and GitHub |
| Target hosting | AWS, selected incrementally |

Additional infrastructure such as Redis, Kafka, and LiveKit will be introduced only when the relevant development phase justifies it.

## Architecture and Development

Enishi uses an incremental microservices approach. Services will be introduced as features require them rather than creating every service upfront.

The first backend service is the **Trip Service**, responsible for trip creation, membership, invitations, and trip-level roles.

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md), [`docs/API_CONTRACTS.md`](docs/API_CONTRACTS.md), [`docs/DECISIONS.md`](docs/DECISIONS.md), and [`docs/SETUP.md`](docs/SETUP.md) for the project's technical direction and development conventions.

## Project Status

**Stage:** Initial repository and development environment setup.

Application code, integrations, automated tests, and deployment will be added incrementally.

## Scope Boundaries

Enishi is a travel-planning and collaboration application, not a booking or payment platform. Payments, expense splitting, a public social feed, and unrestricted AI access to application data are outside the planned scope.

Live video hangouts are a later-stage feature.
