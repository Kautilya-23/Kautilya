# Database (MySQL 8)

1. Install/start MySQL 8 and make sure a user can create schemas (or run `schema.sql` once as root).
2. Set env vars (see `../.env.example`): `DB_HOST DB_PORT DB_NAME DB_USERNAME DB_PASSWORD`.
3. Start the backend: Hibernate (`ddl-auto=update`) creates/updates tables and **keeps data across restarts**.
4. Doctors (Dr. Mehta, Dr. Iyer, Dr. Khan) are seeded on first start by `DataSeeder` (only if the table is empty).

Tables: `patients`, `doctors`, `appointments` (FK patient_id, doctor_id), `consultations` (FK appointment_id, UNIQUE).
`schema.sql` is a reference/optional manual setup. For production you would replace `ddl-auto=update` with Flyway migrations.
