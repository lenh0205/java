
# ddl-auto

```bash - application.properties
spring.jpa.hibernate.ddl-auto=update
```

1. none
Hibernate does not attempt any schema generation or changes.
This is the safest option for production environments where the schema is managed manually.
2. update
Hibernate compares the current entity model to the existing database schema and applies updates to match the model.
Pros:
Convenient for development; your database schema evolves automatically as your entity models change.
Allows adding new tables, columns, and constraints.
Cons:
Can lead to unexpected schema changes.
Cannot handle all changes (e.g., renaming columns/tables).
Should not be used in production due to the risk of data loss or unintended schema changes.
3. create
Hibernate drops the existing schema and creates a new one based on the entity model.
Pros:
Ideal for initial development and testing when you don’t care about preserving data.
Cons:
Completely erases the database every time the application restarts.
Not suitable for production or when data retention is necessary.
4. create-drop
Similar to create, but it drops the schema when the application stops.
Pros:
Useful for running integration tests where you need a clean slate for each test session.
Cons:
Completely removes data after the application shuts down.
5. validate
Hibernate only validates that the schema matches the entity model but does not apply any changes.
Pros:
Useful for production environments to ensure the schema is correctly configured.
Cons:
Will cause the application to fail startup if there are mismatches.