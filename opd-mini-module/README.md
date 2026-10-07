# OPD Mini Module (Equilym practical)

Small Out-Patient Department workflow: **Patient registration → Appointment booking → Consultation → Patient history.**

## Features
Register / list / search patients (name or phone) · book appointment with a doctor · today's appointments ·
open consultation, record 2 vitals + notes, complete it (cannot be completed twice) · completed-consultation history per patient.
UI has validation messages, loading / empty / error / success states and a confirmation before completing.

## Tech stack
Java 17+, Spring Boot 3.3, Spring Data JPA / Hibernate, Jakarta Validation, Maven, MySQL 8, Angular 17 (standalone components, HttpClient), JUnit 5 + Mockito + `@WebMvcTest`.

## Architecture
`Angular → REST Controller → Service → Repository → Hibernate/JPA → MySQL` (see `docs/architecture.md`). DTOs (records) separate API from entities; `@RestControllerAdvice` returns uniform JSON errors.

## Setup & run
1. **Database:** start MySQL 8 (see `database/README.md`). Tables are auto-created; doctors are seeded.
2. **Env vars** (defaults in brackets, see `.env.example`): `DB_HOST`[localhost] `DB_PORT`[3306] `DB_NAME`[opd_db] `DB_USERNAME`[root] `DB_PASSWORD`[empty] `CORS_ORIGIN`[http://localhost:4200]
3. **Backend:** `cd backend && mvn spring-boot:run` → http://localhost:8080
4. **Frontend:** `cd frontend && npm install && npm start` → http://localhost:4200
5. **Tests:** `cd backend && mvn test` (no database needed; services use mocks, controllers use MockMvc)

## API
See `docs/api.md`. Summary: `POST/GET /api/patients`, `GET /api/doctors`, `POST /api/appointments`, `GET /api/appointments/today`,
`POST /api/appointments/{id}/consultation`, `PATCH /api/appointments/{id}/complete`, `GET /api/patients/{id}/consultations`.

## Sample data
Doctors seeded: Dr. Mehta (General Medicine), Dr. Iyer (Pediatrics), Dr. Khan (Orthopedics). Use fictional patients only, e.g. Rahul Sharma / MALE / 30 / 9876543210.

## Assumptions
Vitals = blood pressure + temperature (°F). No doctor CRUD. Phone is unique and 10 digits. Single combined search box. No login/reports (optional, skipped). `ddl-auto=update` is used for simplicity.

## Future improvements
Spring Security + BCrypt login, pagination, Flyway migrations, Testcontainers integration tests, Angular unit tests, overlapping-slot check for a doctor.

## Interview material
`docs/INTERVIEW.md` (explanations, likely questions, demo script) · `HANDOVER.md` (verification status).
