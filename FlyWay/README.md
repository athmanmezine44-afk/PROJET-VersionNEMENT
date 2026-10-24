# Student Management - Flyway version

This folder contains the same student-management application as the
`Liquibase` folder, with database versioning implemented using **Flyway**.

## Migration history

Flyway discovers SQL files under:

```text
students-api/src/main/resources/db/migration
```

The current migrations are:

| Migration | Purpose |
| --- | --- |
| `V1__init_schema.sql` | Creates `student` and inserts five initial students |
| `V2__add_student_created_at.sql` | Adds `created_at` while preserving existing records |

Flyway records successfully applied migrations in `flyway_schema_history`.
Applied migration files are immutable; later changes must use a new version.

## Configuration

The backend uses:

```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.jpa.hibernate.ddl-auto=validate
```

Flyway owns schema creation and evolution. Hibernate only validates the final
schema against the Java entity mappings.

## Validation

From `FlyWay/students-api`:

```text
mvn clean test
mvn spring-boot:run
```

The local endpoints are:

- API: `http://localhost:8081/api/students`
- H2 console: `http://localhost:8081/h2-console`

Inspect the migration history with:

```sql
SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

## Comparison with Liquibase

The Java code, REST API, initial data, and final database schema are equivalent
to the `Liquibase` folder. Only the migration mechanism differs:

| Concern | Flyway | Liquibase |
| --- | --- | --- |
| Dependency | `flyway-core` | `liquibase-core` |
| Migration location | `db/migration` | `db/changelog` |
| Migration format | Versioned SQL files | YAML changesets |
| History table | `flyway_schema_history` | `DATABASECHANGELOG` |
| Configuration | `spring.flyway.*` | `spring.liquibase.*` |
