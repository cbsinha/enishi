# Clerk Authentication

## 1. Overview

Enishi uses [Clerk](https://clerk.com/) for user authentication.

The frontend uses Clerk to authenticate users and obtain session tokens. Backend services validate those tokens before allowing access to protected API endpoints.

The Trip Service uses Spring Security OAuth2 Resource Server with JWT support to validate Clerk-issued session tokens.

The authentication flow is:

1. A user signs in through Clerk in the frontend.
2. The frontend obtains a Clerk session token.
3. The frontend sends the token to the backend using the `Authorization: Bearer <token>` header.
4. The Trip Service validates the token's signature and claims using Clerk's JSON Web Key Set (JWKS).
5. Spring Security establishes an authenticated principal.
6. The application obtains the authenticated user's Clerk ID from the verified JWT `sub` claim.

Clerk handles authentication. Enishi remains responsible for application-level authorization, such as determining whether a user can access or modify a particular trip.

## 2. Clerk Application Configuration

Both developers use the same Enishi Clerk development application.

The current development application uses the following issuer:

```text
https://wondrous-cod-4957.clerk.accounts.dev
```

The corresponding JWKS endpoint is:

```text
https://wondrous-cod-4957.clerk.accounts.dev/.well-known/jwks.json
```

The JWKS endpoint publishes the public keys used to verify JWT signatures. The backend retrieves these keys to validate incoming tokens.

### Token expectations

The Trip Service expects Clerk session JWTs with:

- A valid cryptographic signature.
- The configured Clerk issuer in the `iss` claim.
- A valid expiration time (`exp`).
- A valid JWT structure and required claims.
- A subject (`sub`) identifying the authenticated Clerk user.

The current Clerk session tokens use RS256 signing.

The backend must validate tokens against the expected issuer. Tokens from another issuer must not be accepted.

**Important:** Use Clerk session tokens for API authentication. Do not assume that every token issued by Clerk, such as an unrelated OAuth token, is suitable for this purpose.

## 3. Environment Variables

Environment-specific configuration must remain outside tracked source files.

The Trip Service uses the following environment variables:

| Variable | Purpose |
|---|---|
| `CLERK_ISSUER_URI` | Expected issuer of Clerk JWTs |
| `CLERK_JWK_SET_URI` | Clerk JWKS endpoint used for signature verification |
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of permitted frontend origins |
| `SPRING_DATASOURCE_URL` | PostgreSQL connection URL |
| `SPRING_DATASOURCE_USERNAME` | PostgreSQL username |
| `SPRING_DATASOURCE_PASSWORD` | PostgreSQL password |

The Clerk variables are mapped in `application.properties`:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=${CLERK_ISSUER_URI}
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${CLERK_JWK_SET_URI}
```

CORS configuration:

```properties
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:4200}
```

The default CORS origins support local development. Set `CORS_ALLOWED_ORIGINS` explicitly for other environments.

### Local development — Windows PowerShell

Set the Clerk variables in the PowerShell session before starting the Trip Service:

```powershell
$env:CLERK_ISSUER_URI = "https://wondrous-cod-4957.clerk.accounts.dev"

$env:CLERK_JWK_SET_URI = "https://wondrous-cod-4957.clerk.accounts.dev/.well-known/jwks.json"

$env:CORS_ALLOWED_ORIGINS = "http://localhost:3000,http://localhost:4200"
```

These variables are session-specific. They must be configured again in a new PowerShell session, or added to the appropriate local IntelliJ run configuration.

For normal database-backed execution, also configure the PostgreSQL environment variables.

**Never commit database credentials, Clerk secret keys, or other private credentials to Git.**

The Clerk publishable key may be used by the frontend as intended by Clerk. The Clerk secret key must remain server-side and must never be exposed in frontend code.

## 4. Running the Trip Service Locally

### Authentication testing without PostgreSQL

The Trip Service contains a local Spring profile configuration in:

```text
services/trip-service/src/main/resources/application-local.properties
```

This configuration disables database and Flyway auto-configuration for local authentication testing.

The file is intentionally excluded from Git and should remain local.

After configuring the Clerk environment variables, run:

```powershell
cd services/trip-service

.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

This starts the Trip Service without requiring a Neon database connection.

This local profile is intended for authentication testing. It does not disable JWT validation or make protected API endpoints public.

### Normal database-backed execution

For normal execution, configure the PostgreSQL environment variables along with the Clerk variables, then start the service without the `local` profile:

```powershell
.\mvnw.cmd spring-boot:run
```

Flyway manages database migrations when database auto-configuration is enabled.

## 5. Backend Security Configuration

Security rules are defined in:

```text
services/trip-service/src/main/java/com/enishi/tripservice/config/SecurityConfig.java
```

The current access rules are:

| Endpoint pattern | Access |
|---|---|
| `/actuator/health` | Public |
| Other `/actuator/**` endpoints | Denied |
| `/api/**` | Authenticated requests only |
| All other endpoints | Denied |

The Trip Service uses stateless authentication.

- HTTP sessions are not used to maintain authentication state.
- CSRF protection is disabled because the API uses stateless bearer-token authentication.
- Protected API requests must provide a valid bearer token.
- CORS is configured for the API endpoints.
- Only configured frontend origins are allowed.

CORS is a browser access-control mechanism. It is not a replacement for authentication or authorization.

## 6. JWT Validation

Spring Security OAuth2 Resource Server handles bearer-token processing and JWT authentication.

The configured issuer and JWKS endpoint allow the backend to validate Clerk JWTs.

The backend must reject tokens that are:

- Missing from a protected request.
- Malformed or structurally invalid.
- Incorrectly signed.
- Expired.
- Issued by an unexpected issuer.

The backend must never treat a client-supplied user ID as proof of identity.

The authenticated user's identity must be obtained from the verified JWT principal.

## 7. Accessing the Authenticated User

The current diagnostic endpoint is:

```http
GET /api/v1/auth/me
```

It requires authentication and returns the authenticated user's Clerk subject.

Example response:

```json
{
  "userId": "user_123456789"
}
```

The endpoint is implemented in:

```text
services/trip-service/src/main/java/com/enishi/tripservice/controller/AuthController.java
```

The controller obtains the authenticated `Jwt` principal through Spring Security's `@AuthenticationPrincipal` annotation and reads its `sub` claim.

The returned user ID is derived from the verified token, not from request parameters or request-body data.

## 8. Frontend Integration

The frontend must obtain a Clerk session token and include it in requests to protected backend endpoints.

The request format is:

```http
GET /api/v1/auth/me
Authorization: Bearer <CLERK_SESSION_TOKEN>
```

The frontend must not send a user ID as a substitute for a valid authentication token.

The backend remains responsible for validating the token, regardless of frontend route guards or UI authentication state.

The frontend's Clerk publishable key is not a substitute for the session token sent to the backend.

## 9. Authentication vs Authorization

Authentication and authorization are separate responsibilities.

**Authentication** establishes the identity of the user through a verified Clerk JWT.

**Authorization** determines what that authenticated user is permitted to do.

For example, a valid Clerk token establishes who is making a request. It does not automatically grant access to every trip in Enishi.

Trip ownership, membership, invitations, and access restrictions must be enforced by backend application logic in the relevant services.

A user must never gain access to another user's resources simply by supplying a different user ID or resource ID.

Trip-level authorization is outside the scope of the initial Clerk JWT validation implementation.

## 10. Testing

Run the Trip Service tests from its directory:

```powershell
.\mvnw.cmd test
```

The current authentication tests cover:

- Unauthenticated requests to protected endpoints.
- Access using an authenticated JWT principal.
- Acceptance of a valid JWT.
- Rejection of a JWT with an incorrect issuer.
- Rejection of an expired JWT.
- Rejection of a JWT with an invalid signature.

The MVC security test uses Spring Security's JWT test support to simulate an authenticated principal. It does not independently verify a real token signature.

The decoder tests exercise JWT validation behavior using locally generated signing keys.

Runtime integration testing with a real Clerk session token should also be performed when verifying the Clerk application configuration.

## 11. Security Considerations

- Never commit Clerk secret keys or database credentials.
- Never expose Clerk secret keys in Angular frontend code.
- Keep issuer and JWKS configuration environment-specific.
- Validate JWT signatures and claims on the backend.
- Never trust user IDs supplied directly by clients.
- Do not treat frontend route guards as backend security controls.
- Do not confuse successful authentication with authorization to access a resource.
- Keep local-only configuration files out of version control.
- Use HTTPS for deployed frontend and backend communication.

## 12. Current Scope and Future Work

The current implementation establishes Clerk JWT authentication in the Trip Service.

It provides a verified user identity to backend request handlers and protects API endpoints.

The following responsibilities remain separate implementation tasks:

- Trip ownership and membership authorization.
- Trip invitation and access-control rules.
- User profile management.
- Resource-level authorization tests.
- Authentication integration in additional backend services, where required.

These features should build on the verified identity established by the authentication layer rather than implementing separate, competing authentication mechanisms.
