# Interview preparation (based on this actual code)

## 30 seconds
An OPD mini-module: a receptionist registers patients and books appointments with seeded doctors; the doctor opens today's appointment, records blood pressure and temperature plus notes, and completes the consultation; each patient has a history of completed consultations. Angular → REST → Spring Boot services → JPA/Hibernate → MySQL.

## 1 minute
Four tables: patients, doctors, appointments, consultations. Controllers only handle HTTP, services hold the rules (patient must exist, consultation can't be completed twice, "today" filters by a real date range), repositories are Spring Data interfaces. DTOs are records so entities never leak and there is no circular JSON. One `@RestControllerAdvice` maps exceptions to 400/404/409/500 JSON. Angular has three pages and three services that centralise HttpClient calls and show friendly errors.

## Functional flow
Register patient (`POST /patients`) → book (`POST /appointments`) → today list (`GET /appointments/today`) → open consultation page → save vitals (`POST .../consultation`) → complete (`PATCH .../complete`, appointment becomes COMPLETED) → history (`GET /patients/{id}/consultations`).

## Where things are in the code
- **Entities:** `Patient`, `Doctor`, `Appointment` (`@ManyToOne` LAZY to patient and doctor), `Consultation` (`@OneToOne` to appointment, unique FK, `completedAt` null until done, `isCompleted()`).
- **Services:** `PatientService` (duplicate phone → `ConflictException`), `AppointmentService` (`book`, `today`, `find`), `ConsultationService` (`save`, `complete`, `history`).
- **Exceptions:** `ResourceNotFoundException`→404, `ConflictException`→409, `BadRequestException`→400, validation→400, anything else→500 with a generic message (stack trace only in logs).
- **Config:** `WebConfig` (CORS limited to one origin from `app.cors.origin`), `DataSeeder` (doctors).

## OOP
Encapsulation: private entity fields, getters/setters, `Consultation.isCompleted()` hides the "completed" rule. Abstraction/interfaces: repositories are interfaces, Spring generates the implementation. Composition + DI: services receive repositories via constructor. Polymorphism: custom exceptions extend `RuntimeException`; the handler treats them uniformly. (No forced inheritance.)

## SOLID
SRP: controller/service/repository each have one job. OCP: new business rules go into services without touching controllers. DIP: services depend on repository abstractions injected by Spring. (LSP/ISP naturally satisfied by small interfaces; I did not invent extra abstractions.)

## Design patterns
| Pattern | Where | Why |
|---|---|---|
| Repository | `*Repository` | hide persistence behind an interface |
| Service Layer | `*Service` | one place for rules + transactions |
| DTO | `Dto.java` records | stable API, no entity leakage |
| Dependency Injection | constructors everywhere | testable, loosely coupled |

## JPA / Hibernate / MySQL
JPA is the specification; Hibernate is the implementation. `@Entity` maps class→table, `@ManyToOne`+`@JoinColumn` creates the FK, `fetch=LAZY` avoids loading unused rows, `open-in-view=false` means mapping to DTO happens inside `@Transactional`. Saving an appointment: service validates patient/doctor, `repo.save` persists, INSERT is executed at commit. `ddl-auto=update` keeps data between restarts. Indexes: `patients.name`, `appointments.appointment_date_time`; phone has a UNIQUE constraint. To speed search: prefix `LIKE 'x%'` or a FULLTEXT index; pagination for big lists.

## Validation & errors
Jakarta annotations on request records (`@NotBlank @Pattern @Min @Max @DecimalMin`) → `MethodArgumentNotValidException` → 400 listing field messages. Business rules in services → 404/409. Frontend uses template-driven validation (required/pattern/min/max) plus `errorMessage()` for server messages and "server unreachable".

## Angular
Standalone components with routing (`/patients`, `/appointments`, `/consultation/:id`). Services in `api.service.ts` wrap `HttpClient` and return Observables; components subscribe and set loading/success/error flags. `FormsModule` handles form state (`ng-invalid ng-touched` styling). CORS is configured on the backend.

## Likely questions (short answers)
- **Why Spring Boot?** auto-config, embedded server, starters. **Why constructor injection?** immutable, testable, required deps explicit.
- **@RestController / @Service / @Repository?** web layer returning JSON / business layer / persistence layer (exception translation).
- **JPA vs Hibernate?** spec vs implementation. **What is @ManyToOne?** many appointments → one patient, FK column.
- **Why 201?** a new resource was created. **GET vs POST?** safe read vs create. **Why DTOs?** decouple API from schema, avoid lazy/circular serialization.
- **Why relational?** strong relationships and constraints (patient→appointment→consultation).
- **Scale it?** pagination, caching reads, Flyway, read replicas, auth, containerise — only when needed.
- **Challenges:** preventing double completion (409 + UI lock), "today" boundary (start-of-day to end-of-day range), avoiding lazy-loading errors (mapping inside the transaction).

## Demo script (2–3 minutes)
1. Open http://localhost:4200 (Patients page).
2. Register **Rahul Sharma / MALE / 30 / 9876543210** → green success message.
3. Show it in the list; search "Rahul" and then "98765".
4. Try the same phone again → error message (409) shown in the UI.
5. Appointments page → choose Rahul, Dr. Mehta, date/time today → **Book**.
6. Today's list shows it as BOOKED → **Open consultation**.
7. Enter BP `120/80`, Temp `98.6`, notes → **Complete consultation** → confirm.
8. See the locked completed view and the patient's history table below.
9. Patients page → **History** on Rahul → completed consultation is listed.
10. (Optional) stop backend and refresh → friendly "cannot reach the server" message.
