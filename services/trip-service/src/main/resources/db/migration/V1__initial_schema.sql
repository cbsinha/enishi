-- Initial migration for the Trip Service.
-- Creates the trip_service schema and its initial metadata table.
-- Domain tables will be added in future versioned migrations.
CREATE SCHEMA IF NOT EXISTS trip_service;
CREATE TABLE trip_service.schema_metadata (
    service_name VARCHAR(100) PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO trip_service.schema_metadata (service_name)
VALUES ('trip-service');