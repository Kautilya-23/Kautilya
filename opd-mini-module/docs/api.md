# API Reference (base URL `http://localhost:8080/api`)

Error body (all errors): `{"timestamp":"...","status":404,"message":"Patient not found","path":"/api/patients/10"}`

| Endpoint | Method | Purpose | Success | Errors |
|---|---|---|---|---|
| `/patients` | POST | Register patient | 201 | 400 validation, 409 duplicate phone |
| `/patients?q=` | GET | List all, or search name/phone (partial, case-insensitive name) | 200 | – |
| `/patients/{id}/consultations` | GET | Completed consultations of a patient (newest first) | 200 | 404 |
| `/doctors` | GET | Seeded doctors | 200 | – |
| `/appointments` | POST | Book appointment | 201 | 400, 404 patient/doctor |
| `/appointments/today` | GET | Appointments whose date is today | 200 | – |
| `/appointments/{id}` | GET | One appointment | 200 | 404 |
| `/appointments/{id}/consultation` | POST | Save vitals + notes (draft) | 201 | 400, 404, 409 completed |
| `/appointments/{id}/consultation` | GET | Read consultation | 200 | 404 |
| `/appointments/{id}/complete` | PATCH | Complete consultation | 200 | 400 no vitals, 404, 409 already completed |

## Examples
```bash
# 1 create patient
curl -i -X POST localhost:8080/api/patients -H "Content-Type: application/json" \
  -d '{"name":"Rahul Sharma","gender":"MALE","age":30,"phoneNumber":"9876543210"}'
# 2 list / search
curl localhost:8080/api/patients
curl "localhost:8080/api/patients?q=rahul"
curl "localhost:8080/api/patients?q=98765"
# 3 doctors, book appointment (use today's date)
curl localhost:8080/api/doctors
curl -i -X POST localhost:8080/api/appointments -H "Content-Type: application/json" \
  -d "{\"patientId\":1,\"doctorId\":1,\"appointmentDateTime\":\"$(date +%Y-%m-%dT%H:%M:00)\"}"
curl localhost:8080/api/appointments/today
# 4 consultation
curl -i -X POST localhost:8080/api/appointments/1/consultation -H "Content-Type: application/json" \
  -d '{"bloodPressure":"120/80","temperature":98.6,"notes":"Fever for 2 days"}'
curl -i -X PATCH localhost:8080/api/appointments/1/complete
curl -i -X PATCH localhost:8080/api/appointments/1/complete      # -> 409
curl localhost:8080/api/patients/1/consultations
```

## Request bodies
- Patient: `name` 2–100 chars, `gender` MALE|FEMALE|OTHER, `age` 0–120, `phoneNumber` 10 digits.
- Appointment: `patientId`, `doctorId`, `appointmentDateTime` (ISO local, e.g. `2026-10-07T10:30:00`) – all required.
- Consultation: `bloodPressure` like `120/80`, `temperature` 90–110, `notes` ≤ 1000 chars.
