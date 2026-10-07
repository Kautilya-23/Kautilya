package com.equilym.opd.service;
import com.equilym.opd.dto.Dto.*;
import com.equilym.opd.entity.*;
import com.equilym.opd.exception.ResourceNotFoundException;
import com.equilym.opd.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
@Service
public class AppointmentService {
    private final AppointmentRepository appointments; private final PatientRepository patients; private final DoctorRepository doctors;
    public AppointmentService(AppointmentRepository a, PatientRepository p, DoctorRepository d) { appointments = a; patients = p; doctors = d; }
    @Transactional
    public AppointmentResponse book(CreateAppointmentRequest r) {
        Appointment a = new Appointment();
        a.setPatient(patients.findById(r.patientId()).orElseThrow(() -> new ResourceNotFoundException("Patient not found")));
        a.setDoctor(doctors.findById(r.doctorId()).orElseThrow(() -> new ResourceNotFoundException("Doctor not found")));
        a.setAppointmentDateTime(r.appointmentDateTime());
        return toDto(appointments.save(a));
    }
    @Transactional(readOnly = true)
    public List<AppointmentResponse> today() {
        LocalDate d = LocalDate.now();
        return appointments.findByAppointmentDateTimeBetweenOrderByAppointmentDateTime(d.atStartOfDay(), d.plusDays(1).atStartOfDay().minusNanos(1)).stream().map(AppointmentService::toDto).toList();
    }
    @Transactional(readOnly = true)
    public AppointmentResponse get(Long id) { return toDto(find(id)); }
    @Transactional(readOnly = true)
    public List<DoctorResponse> doctors() { return doctors.findAll().stream().map(d -> new DoctorResponse(d.getId(), d.getName(), d.getSpecialization())).toList(); }
    Appointment find(Long id) { return appointments.findById(id).orElseThrow(() -> new ResourceNotFoundException("Appointment not found")); }
    static AppointmentResponse toDto(Appointment a) {
        return new AppointmentResponse(a.getId(), a.getPatient().getId(), a.getPatient().getName(), a.getDoctor().getId(), a.getDoctor().getName(), a.getAppointmentDateTime(), a.getStatus().name());
    }
}
