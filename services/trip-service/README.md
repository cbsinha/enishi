# Trip Service

Spring Boot microservice responsible for trip-related functionality in Enishi.

## Prerequisites

- Java 25
- Maven (the included Maven Wrapper can be used)
- Access to a PostgreSQL database, such as the Enishi development database on Neon

## Database Configuration

The service uses PostgreSQL through Spring JDBC. Database credentials must be supplied through environment variables and must not be hardcoded or committed to source control.

Configure the following environment variables before starting the service:

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC URL of the PostgreSQL database |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |

For Neon, the JDBC URL should use the format:

`jdbc:postgresql://HOST/DATABASE?sslmode=require`

Use the host and database name provided by Neon. Keep credentials private and never commit `.env.local` or other files containing secrets.

### PowerShell Example

Set the variables in your current PowerShell session using values obtained securely from your database provider:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://HOST/DATABASE?sslmode=require"
$env:SPRING_DATASOURCE_USERNAME = "YOUR_DATABASE_USERNAME"
$env:SPRING_DATASOURCE_PASSWORD = "YOUR_DATABASE_PASSWORD"
```

Replace the placeholders with your actual connection details. Do not commit real credentials.

These environment variables apply only to the current terminal session.

## Database Migrations

Flyway manages versioned database migrations automatically when the application starts.

Migration files are located in:

`src/main/resources/db/migration/`

Use the following naming convention for new migrations:

- `V1__initial_schema.sql`
- `V2__add_trip_tables.sql`
- `V3__add_trip_indexes.sql`

Use the next available version number for each new migration. Do not modify a migration that has already been applied to a shared database; create a new versioned migration instead.

The initial migration creates the `trip_service` schema and its `schema_metadata` table.

### Schema Ownership

- `trip_service` is owned by the Trip Service and holds its database objects.
- `public` is configured as Flyway's default schema for migration tracking.
- Flyway maintains its migration history in `public.flyway_schema_history`.
- Future trip-related tables should be added under `trip_service` through new migrations.

## Run the Service

From the repository root, configure the required environment variables and run:

```powershell
.\services\trip-service\mvnw.cmd -f services\trip-service\pom.xml spring-boot:run
```

The service starts on port `8080` by default.

## Verify the Service

Check the health endpoint:

`http://localhost:8080/actuator/health`

A healthy running service should report an `UP` status.

## Troubleshooting

- **Missing environment variables:** Verify that all three `SPRING_DATASOURCE_*` variables are set in the terminal used to start the service.
- **Database connection errors:** Check the hostname, database name, username, password, network access, and SSL configuration.
- **Migration errors:** Inspect the application logs for Flyway validation or SQL errors. Do not edit an already-applied migration to resolve a schema change; add a new migration instead.
- **Secrets:** Never share database connection strings containing passwords or commit credentials to Git.

## Scope

This service currently establishes database connectivity, migration management, and the initial schema. Trip-management endpoints and additional domain tables can be introduced in subsequent changes.
