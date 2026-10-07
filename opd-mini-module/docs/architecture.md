# Architecture

```
Angular (pages -> services -> HttpClient)
        |  JSON over HTTP (CORS: http://localhost:4200 -> :8080)
        v
REST Controller   (HTTP concerns only: routes, status codes, @Valid)
        v
Service           (business rules, @Transactional, entity <-> DTO mapping)
        v
Repository        (Spring Data JPA interfaces)
        v
Hibernate / JPA   (entity mapping, SQL generation)
        v
MySQL
```

## Request flow: "Complete consultation"
1. Angular `ConsultationComponent.complete()` asks for confirmation, then `POST /api/appointments/{id}/consultation` (save vitals) and `PATCH /api/appointments/{id}/complete`.
2. `AppointmentController` receives the request; `@Valid` checks `ConsultationRequest` (BP format, temperature range).
3. `ConsultationService` (transactional) loads the appointment (404 if missing), loads/creates the consultation, rejects it with 409 if already completed.
4. On complete it sets `completedAt` and appointment status `COMPLETED`; Hibernate flushes UPDATE/INSERT on commit.
5. The entity is mapped to `ConsultationResponse` inside the transaction (open-in-view is off) and returned as JSON.
6. Any exception goes to `GlobalExceptionHandler` -> `{timestamp,status,message,path}`; Angular shows `message` (never a stack trace).

## Data model
`Patient 1—* Appointment *—1 Doctor`, `Appointment 1—0..1 Consultation`.
A consultation reaches its patient through its appointment (history query: `findByAppointmentPatientId...`).

## Key decisions
- Vitals: `bloodPressure` ("120/80") and `temperature` (°F, 90–110).
- Doctors are seeded; no Doctor CRUD (not in scope).
- Phone number is unique (10 digits); duplicate -> 409.
- Single combined patient search `GET /api/patients?q=` matches name OR phone.
- DTOs are Java records in `dto/Dto.java`; entities are never serialized (no circular JSON).
- No login/reports: optional, deliberately skipped until the core is verified.
