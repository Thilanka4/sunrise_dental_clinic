# Phase 1 Architecture

The application follows a strict three-tier structure:

- Presentation: REST controllers under `/api/**` and Thymeleaf web controllers/views.
- Business: service classes containing validation orchestration and business rules.
- Data access: Spring Data JPA repositories backed by MySQL.

The relational model separates staff users, patients, treatments, appointments, and bills. An appointment references one patient and one treatment; a bill references one appointment. This keeps repeated patient and treatment data out of appointment records and gives each concern a clear ownership boundary.

Phase 1 establishes the model and infrastructure. Authentication, service operations, validation DTOs, patterns, database routines, and UI flows are intentionally added in later phases.
