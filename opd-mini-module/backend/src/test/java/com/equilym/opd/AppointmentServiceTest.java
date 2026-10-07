package com.equilym.opd;
import com.equilym.opd.dto.Dto.*;
import com.equilym.opd.entity.*;
import com.equilym.opd.exception.ResourceNotFoundException;
import com.equilym.opd.repository.*;
import com.equilym.opd.service.AppointmentService;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
class AppointmentServiceTest {
    AppointmentRepository appts = mock(AppointmentRepository.class);
    PatientRepository patients = mock(PatientRepository.class);
    DoctorRepository doctors = mock(DoctorRepository.class);
    AppointmentService service = new AppointmentService(appts, patients, doctors);

    @Test void unknownPatientThrowsNotFound() {
        when(patients.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.book(new CreateAppointmentRequest(1L, 1L, LocalDateTime.now())));
    }
    @Test void booksAppointment() {
        Patient p = new Patient(); p.setName("A"); Doctor d = new Doctor("Dr. Mehta", "GM");
        when(patients.findById(1L)).thenReturn(Optional.of(p)); when(doctors.findById(1L)).thenReturn(Optional.of(d));
        when(appts.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));
        assertEquals("BOOKED", service.book(new CreateAppointmentRequest(1L, 1L, LocalDateTime.now())).status());
    }
    @Test void todayQueriesOnlyTodaysRange() {
        when(appts.findByAppointmentDateTimeBetweenOrderByAppointmentDateTime(any(), any())).thenReturn(List.of());
        service.today();
        verify(appts).findByAppointmentDateTimeBetweenOrderByAppointmentDateTime(
            argThat(s -> s.toLocalDate().equals(java.time.LocalDate.now()) && s.toLocalTime().equals(java.time.LocalTime.MIDNIGHT)),
            argThat(e -> e.toLocalDate().equals(java.time.LocalDate.now())));
    }
}
