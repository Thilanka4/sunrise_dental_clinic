# Sunrise Dental Clinic — Appointment & Patient Management System
CIS6003 Advanced Programming — Cardiff Met / ICBT
Spring Boot 3 · MySQL · REST API + Thymeleaf UI

[![CI](https://github.com/Thilanka4/sunrise_dental_clinic/actions/workflows/ci.yml/badge.svg)](https://github.com/Thilanka4/sunrise_dental_clinic/actions/workflows/ci.yml)

## Prerequisites

- Java 17+
- Maven
- MySQL 8

## 1. Create the database and schema

```bash
mysql -u root -p -e "CREATE DATABASE sunrise_dental;"
mysql -u root -p sunrise_dental < src/main/resources/db/schema.sql
mysql -u root -p sunrise_dental < src/main/resources/db/phase6-advanced-db-objects.sql
```

If your MySQL server has binary logging enabled, the second script may need this first
(see the comment at the top of that file for details):

```sql
SET GLOBAL log_bin_trust_function_creators = 1;
```

## 2. Configure credentials

The app reads DB credentials and the default staff login from environment variables
(see `src/main/resources/application.properties`):

| Variable | Default | Purpose |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/sunrise_dental` | JDBC URL |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | *(none)* | MySQL password — **required** |
| `STAFF_DEFAULT_USERNAME` | `admin` | Seeded staff login username |
| `STAFF_DEFAULT_PASSWORD` | `ChangeMe123!` | Seeded staff login password |

Set at least `DB_PASSWORD` before running, e.g. in PowerShell:

```powershell
$env:DB_PASSWORD = "your-mysql-password"
```

## 3. Run

```bash
mvn spring-boot:run
```

Visit http://localhost:8080, sign in with the staff credentials above, and use the in-app
**Help** page for a walkthrough of the main features.

## Tests

```bash
mvn test
```

Runs against an in-memory H2 database (`src/test/resources/application.properties`) —
never touches the MySQL database set up above.
