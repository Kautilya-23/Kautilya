package com.equilym.opd.repository;
import com.equilym.opd.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByAppointmentDateTimeBetweenOrderByAppointmentDateTime(LocalDateTime from, LocalDateTime to);
}
