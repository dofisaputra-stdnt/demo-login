# Database Migration

This project uses Flyway for database migrations. The migrations are organized into two directories:

## Directory Structure

- `master/`: Contains migrations for core functionalities (users, authentication, etc.)
- `tenant/`: Contains migrations for tenant-specific functionalities (products, etc.)

## Migration Naming Convention

Migration files follow the Flyway naming convention:

```
V<VERSION>__<DESCRIPTION>.sql
```

For example:

- `V1__init.sql`
- `V2__product_init.sql`

## Important Rules

1. **All migration versions must be unique across ALL directories**

   - Even though migrations are in different folders, Flyway treats them as a single sequence
   - No duplicate version numbers allowed, even in different directories

2. **Follow sequential version numbering**

   - `master/` migrations: V1, V3, V5, etc.
   - `tenant/` migrations: V2, V4, V6, etc.
   - Or use a more explicit scheme like:
     - `master/` migrations: V1_0_0, V1_0_1, etc.
     - `tenant/` migrations: V2_0_0, V2_0_1, etc.

3. **Never modify existing migrations**
   - Once a migration is applied to a database, never change it
   - Instead, create a new migration to make further changes

## Running Migrations

Migrations are automatically applied when the application starts up, according to the configuration in `application.properties`.
