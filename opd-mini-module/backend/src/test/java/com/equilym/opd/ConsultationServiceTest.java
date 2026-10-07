package com.equilym.opd;
import com.equilym.opd.dto.Dto.*;
import com.equilym.opd.entity.*;
import com.equilym.opd.exception.*;
import com.equilym.opd.repository.*;
import com.equilym.opd.service.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
class ConsultationServiceTest {
    ConsultationRepository repo = mock(ConsultationRepository.class);
    AppointmentRepository apptRepo = mock(AppointmentRepository.class);
    PatientRepository patientRepo = mock(PatientRepository.class);
    ConsultationService service = new ConsultationService(repo, new AppointmentService(apptRepo, patientRepo, mock(DoctorRepository.class)), patientRepo);

    private Appointment appointment() {
        Patient p = new Patient(); p.setName("Rahul Sharma"); Doctor d = new Doctor("Dr. Mehta", "General Medicine");
        Appointment a = new Appointment(); a.setPatient(p); a.setDoctor(d); a.setAppointmentDateTime(java.time.LocalDateTime.now());
        return a;
    }

    @Test void completeTwiceThrowsConflict() {
        Appointment a = new Appointment(); when(apptRepo.findById(1L)).thenReturn(Optional.of(a));
        Consultation c = new Consultation(); c.setAppointment(a); c.setCompletedAt(java.time.LocalDateTime.now());
        when(repo.findByAppointmentId(1L)).thenReturn(Optional.of(c));
        assertThrows(ConflictException.class, () -> service.complete(1L));
    }
    @Test void completeWithoutConsultationThrowsBadRequest() {
        when(apptRepo.findById(1L)).thenReturn(Optional.of(new Appointment())); when(repo.findByAppointmentId(1L)).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class, () -> service.complete(1L));
    }
    @Test void unknownAppointmentThrowsNotFound() {
        when(apptRepo.findById(9L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.complete(9L));
    }

    @Test void savesConsultationForExistingAppointment() {
        Appointment a = appointment(); when(apptRepo.findById(1L)).thenReturn(Optional.of(a));
        when(repo.findByAppointmentId(1L)).thenReturn(Optional.empty());
        when(repo.save(any(Consultation.class))).thenAnswer(i -> i.getArgument(0));
        ConsultationResponse r = service.save(1L, new ConsultationRequest("120/80", 98.6, "Fever for 2 days"));
        assertEquals("120/80", r.bloodPressure()); assertFalse(r.completed());
    }
    @Test void saveAfterCompletionThrowsConflict() {
        Appointment a = appointment(); when(apptRepo.findById(1L)).thenReturn(Optional.of(a));
        Consultation c = new Consultation(); c.setAppointment(a); c.setCompletedAt(java.time.LocalDateTime.now());
        when(repo.findByAppointmentId(1L)).thenReturn(Optional.of(c));
        assertThrows(ConflictException.class, () -> service.save(1L, new ConsultationRequest("120/80", 98.6, "x")));
        verify(repo, never()).save(any());
    }
    @Test void completeMarksConsultationAndAppointmentCompleted() {
        Appointment a = appointment(); when(apptRepo.findById(1L)).thenReturn(Optional.of(a));
        Consultation c = new Consultation(); c.setAppointment(a); c.setBloodPressure("120/80"); c.setTemperature(98.6);
        when(repo.findByAppointmentId(1L)).thenReturn(Optional.of(c));
        ConsultationResponse r = service.complete(1L);
        assertTrue(r.completed()); assertEquals(AppointmentStatus.COMPLETED, a.getStatus());
    }
    @Test void historyReturnsCompletedConsultationsOfPatient() {
        Appointment a = appointment(); Consultation c = new Consultation(); c.setAppointment(a); c.setCompletedAt(java.time.LocalDateTime.now());
        when(patientRepo.existsById(5L)).thenReturn(true);
        when(repo.findByAppointmentPatientIdAndCompletedAtNotNullOrderByCompletedAtDesc(5L)).thenReturn(List.of(c));
        assertEquals(1, service.history(5L).size());
    }
    @Test void historyForUnknownPatientThrowsNotFound() {
        when(patientRepo.existsById(77L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.history(77L));
    }
}
