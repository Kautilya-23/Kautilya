package com.equilym.opd.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
public final class Dto {
    private Dto() {}
    public record CreatePatientRequest(
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Pattern(regexp = "MALE|FEMALE|OTHER", message = "must be MALE, FEMALE or OTHER") String gender,
        @NotNull @Min(0) @Max(120) Integer age,
        @NotBlank @Pattern(regexp = "^[0-9]{10}$", message = "must be a 10-digit number") String phoneNumber) {}
    public record PatientResponse(Long id, String name, String gender, int age, String phoneNumber) {}
    public record DoctorResponse(Long id, String name, String specialization) {}
    public record CreateAppointmentRequest(@NotNull Long patientId, @NotNull Long doctorId, @NotNull LocalDateTime appointmentDateTime) {}
    public record AppointmentResponse(Long id, Long patientId, String patientName, Long doctorId, String doctorName,
                                      LocalDateTime appointmentDateTime, String status) {}
    public record ConsultationRequest(
        @NotBlank @Pattern(regexp = "^\\d{2,3}/\\d{2,3}$", message = "format must be like 120/80") String bloodPressure,
        @NotNull @DecimalMin("90.0") @DecimalMax("110.0") Double temperature,
        @Size(max = 1000) String notes) {}
    public record ConsultationResponse(Long id, Long appointmentId, String patientName, String doctorName,
                                       String bloodPressure, Double temperature, String notes,
                                       LocalDateTime appointmentDateTime, LocalDateTime completedAt, boolean completed) {}
}
