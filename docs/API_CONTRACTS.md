# Enishi — API Contracts

## 1. Purpose

This document defines the initial conventions for communication between the Angular frontend and Enishi backend services. Exact endpoint names and request/response schemas will be finalized as each feature is implemented.

## 2. API Conventions

- Use HTTP REST APIs for standard CRUD and query operations.
- Use JSON for request and response bodies unless an integration requires another format.
- Use plural resource names where appropriate.
- Use appropriate HTTP methods: GET, POST, PUT, PATCH, and DELETE.
- Use meaningful HTTP status codes.
- Validate incoming requests on the server.
- Keep controllers thin; place business rules in service/domain layers.
- Document implemented endpoints as they become available.

The `/api` prefix is the initial convention. Versioning can be introduced when compatibility requirements justify it.

## 3. Authentication

Protected endpoints require a valid Clerk-issued bearer token:

`Authorization: Bearer <token>`

The Angular frontend uses `ngx-clerk` v1.0.0 to integrate with Clerk. When a protected API request needs authentication, the frontend can retrieve the active session token through `ClerkService.getToken()`.

The returned token should be sent in the `Authorization` header as a bearer token. The frontend must not log or unnecessarily expose token values.

Spring Security OAuth2 Resource Server validates the token. The service then performs its own authorization checks.

The frontend's ability to retrieve a token does not mean a backend endpoint has accepted it. Backend validation and resource authorization must be implemented and tested separately.

Public endpoints should be explicitly identified rather than assumed to be public.

## 4. Initial Trip Service Scope

The first service will support these capabilities:

| Capability | Initial purpose |
|---|---|
| Current user | Resolve the authenticated Enishi user |
| Create trip | Create a trip owned by the authenticated user |
| List trips | Return trips the authenticated user is permitted to see |
| Get trip | Retrieve a trip after checking access |
| Update trip | Update permitted trip fields |
| Manage membership | Add or remove members according to defined rules |
| Invitations | Create and process trip invitations |

These are planned capabilities, not a claim that the endpoints have already been implemented.

Exact paths, DTOs, validation rules, role names, and invitation lifecycle rules will be finalized during implementation.

## 5. Authorization Requirements

For every protected operation:
- Derive the caller's identity from the verified authentication context.
- Check membership and role requirements.
- Check resource ownership where applicable.
- Reject access to resources the caller is not allowed to view or modify.
- Never rely solely on frontend route guards or hidden UI controls.

Cross-trip access must be tested explicitly.

## 6. Response and Error Handling

Use consistent JSON response structures and error semantics across services. The exact error schema will be selected before the first endpoints are implemented.

Errors should communicate the problem without exposing stack traces, database internals, credentials, or other sensitive details.

## 7. API Evolution

- Keep API contracts synchronized with implementation.
- Update this document when endpoint behavior changes.
- Avoid breaking existing consumers without a deliberate compatibility decision.
- Add pagination, filtering, and sorting conventions when the relevant endpoints need them.

## 8. Future Real-Time Communication

The Collaboration Service is planned to use WebSockets for real-time features such as chat and collaboration updates.

Its connection authentication, authorization, message formats, and failure handling will be specified before that phase begins.
