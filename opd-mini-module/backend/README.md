# Backend (Spring Boot 3.3, Java 17+, Maven)
- Run: `mvn spring-boot:run` · Test: `mvn test` · Package: `mvn clean package`
- Env: `DB_HOST DB_PORT DB_NAME DB_USERNAME DB_PASSWORD CORS_ORIGIN` (see `../.env.example`)
- Packages: `controller` (HTTP) → `service` (rules, transactions) → `repository` (Spring Data) → `entity` (JPA); `dto/Dto.java` (records); `exception` (custom exceptions + `GlobalExceptionHandler`); `config` (CORS, doctor seeder).
- Tests (`src/test`): `PatientServiceTest`, `AppointmentServiceTest`, `ConsultationServiceTest` (Mockito); `PatientControllerTest`, `AppointmentControllerTest` (`@WebMvcTest`).
