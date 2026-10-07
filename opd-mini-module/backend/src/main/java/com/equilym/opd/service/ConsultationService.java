package com.equilym.opd.service;
import com.equilym.opd.dto.Dto.*;
import com.equilym.opd.entity.*;
import com.equilym.opd.exception.*;
import com.equilym.opd.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class ConsultationService {
    private final ConsultationRepository consultations; private final AppointmentService appointmentService; private final PatientRepository patients;
    public ConsultationService(ConsultationRepository c, AppointmentService a, PatientRepository p) { consultations = c; appointmentService = a; patients = p; }
    @Transactional
    public ConsultationResponse save(Long appointmentId, ConsultationRequest r) {
        Appointment a = appointmentService.find(appointmentId);
        Consultation c = consultations.findByAppointmentId(appointmentId).orElseGet(() -> { Consultation n = new Consultation(); n.setAppointment(a); return n; });
        if (c.isCompleted()) throw new ConflictException("Consultation is already completed");
        c.setBloodPressure(r.bloodPressure()); c.setTemperature(r.temperature()); c.setNotes(r.notes());
        return toDto(consultations.save(c));
    }
    @Transactional
    public ConsultationResponse complete(Long appointmentId) {
        Appointment a = appointmentService.find(appointmentId);
        Consultation c = consultations.findByAppointmentId(appointmentId).orElseThrow(() -> new BadRequestException("Enter vitals and notes before completing"));
        if (c.isCompleted()) throw new ConflictException("Consultation is already completed");
        c.setCompletedAt(LocalDateTime.now()); a.setStatus(AppointmentStatus.COMPLETED);
        return toDto(c);
    }
    @Transactional(readOnly = true)
    public ConsultationResponse getForAppointment(Long appointmentId) {
        appointmentService.find(appointmentId);
        return consultations.findByAppointmentId(appointmentId).map(ConsultationService::toDto).orElseThrow(() -> new ResourceNotFoundException("No consultation yet"));
    }
    @Transactional(readOnly = true)
    public List<ConsultationResponse> history(Long patientId) {
        if (!patients.existsById(patientId)) throw new ResourceNotFoundException("Patient not found");
        return consultations.findByAppointmentPatientIdAndCompletedAtNotNullOrderByCompletedAtDesc(patientId).stream().map(ConsultationService::toDto).toList();
    }
    static ConsultationResponse toDto(Consultation c) {
        Appointment a = c.getAppointment();
        return new ConsultationResponse(c.getId(), a.getId(), a.getPatient().getName(), a.getDoctor().getName(), c.getBloodPressure(), c.getTemperature(), c.getNotes(), a.getAppointmentDateTime(), c.getCompletedAt(), c.isCompleted());
    }
}
