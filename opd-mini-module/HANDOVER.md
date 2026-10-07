# Handover / verification status

**Honest status:** all mandatory features are implemented in code, but this was authored in a sandbox with no Maven, MySQL or network access, so **nothing was compiled or executed**. Rows below are marked NOT RUN until you run them locally.

## Requirement traceability
| Requirement | Implementation | Verified |
|---|---|---|
| Add patient | `PatientController.create` → `PatientService.create` | NOT RUN (unit + MockMvc tests written) |
| List patients | `GET /api/patients` | NOT RUN |
| Search name/phone | `GET /api/patients?q=` | NOT RUN (service test written) |
| Book appointment | `POST /api/appointments` → `AppointmentService.book` | NOT RUN |
| Today's appointments | `GET /api/appointments/today` (date-range query) | NOT RUN (test written) |
| Consultation vitals + notes | `POST /api/appointments/{id}/consultation` | NOT RUN |
| Complete consultation / no duplicate | `PATCH .../complete` (409 if repeated) | NOT RUN |
| Patient history | `GET /api/patients/{id}/consultations` | NOT RUN |
| Angular 3 screens | `frontend/src/app/pages/*` | NOT RUN |
| Optional login / reports | skipped on purpose (core unverified) | n/a |

## What to do now (10 minutes)
1. `cd backend && mvn clean test` — fix any compile error (risk areas: Lombok version, records, `@MockBean`).
2. Start MySQL, `mvn spring-boot:run`, run the curl flow in `docs/api.md`.
3. `cd frontend && npm install && npm start`, walk through the demo script in `docs/INTERVIEW.md`.
4. Change the table above to VERIFIED only for what you actually ran.

## Added in this pass
Extra tests (consultation save/complete/history, appointment controller, patient search/history controller), `database/schema.sql`, completed docs, consultation page now shows the patient's history after completion and uses a real confirmation flow, booking message explains non-today dates.
