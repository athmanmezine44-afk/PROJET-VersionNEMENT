# Student Management - Liquibase version

This folder contains the same student-management application as the `FlyWay`
folder, but its database schema is managed with **Liquibase**.

The application stack is:

- Spring Boot 3.2 and Java 17
- Spring Data JPA
- React 19 and Vite
- H2 for local development and tests
- PostgreSQL driver for a future Azure deployment
- Liquibase for database versioning

## What was changed for TP2

The original copy of this project was still using Flyway. It has been converted
to a real Liquibase implementation:

1. `flyway-core` was replaced by `liquibase-core` in
   `students-api/pom.xml`.
2. The Flyway SQL migration under `db/migration` was removed.
3. Liquibase configuration was added to
   `students-api/src/main/resources/application.properties`.
4. A master changelog was created at
   `db/changelog/db.changelog-master.yaml`.
5. Two Liquibase changesets were added:
   - `001-create-student-table.yaml` creates the table and inserts the five
     initial students.
   - `002-add-student-created-at.yaml` adds the `created_at` column with a
     timestamp default. This represents a later schema evolution while
     preserving the existing student records.
6. Integration tests verify both changesets, the evolved schema, the seeded
   data, and the REST CRUD operations.

The Java model and the REST API remain equivalent to the Flyway version. The
`created_at` column is deliberately a database concern for this TP2 step, so it
does not change the student-management UI.

## How Liquibase works here

When Spring Boot starts:

1. Liquibase reads `db.changelog-master.yaml`.
2. The master file includes the changesets in their execution order.
3. Liquibase compares each changeset identifier and checksum with its history.
4. Only changesets that have not already been executed are applied.
5. Liquibase records the result in:
   - `DATABASECHANGELOG`
   - `DATABASECHANGELOGLOCK`
6. Hibernate starts with `ddl-auto=validate` and verifies that the Java mapping
   matches the database schema. Hibernate does not create or modify tables.

This makes the schema reproducible in local H2, tests, and PostgreSQL/Azure.
Applied changesets should not be edited; a new changeset should be created for
each subsequent schema change.

## Useful commands

From `Liquibase/students-api`:

```text
mvn clean test
mvn spring-boot:run
```

The local endpoints are:

- API: `http://localhost:8081/api/students`
- H2 console: `http://localhost:8081/h2-console`

For the H2 console, use:

```text
JDBC URL: jdbc:h2:mem:studentsdb
User: sa
Password: (empty)
```

The Liquibase history can be inspected with:

```sql
SELECT ID, AUTHOR, FILENAME, EXECTYPE
FROM DATABASECHANGELOG
ORDER BY ORDEREXECUTED;
```

## Flyway and Liquibase comparison

Both folders expose the same business API and target the same database result.
Only the migration mechanism differs:

| Concern | FlyWay folder | Liquibase folder |
| --- | --- | --- |
| Dependency | `flyway-core` | `liquibase-core` |
| Migration location | `db/migration` | `db/changelog` |
| Migration format | Versioned SQL files | YAML changesets |
| History table | `flyway_schema_history` | `DATABASECHANGELOG` |
| Lock table | Managed by Flyway | `DATABASECHANGELOGLOCK` |
| Configuration | `spring.flyway.*` | `spring.liquibase.*` |

