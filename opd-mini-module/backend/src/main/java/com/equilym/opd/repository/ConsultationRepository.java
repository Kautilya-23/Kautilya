package com.equilym.opd.repository;
import com.equilym.opd.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByAppointmentId(Long appointmentId);
    List<Consultation> findByAppointmentPatientIdAndCompletedAtNotNullOrderByCompletedAtDesc(Long patientId);
}
