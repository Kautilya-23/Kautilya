package com.equilym.opd.controller;
import com.equilym.opd.dto.Dto.*;
import com.equilym.opd.service.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api")
public class AppointmentController {
    private final AppointmentService appointments; private final ConsultationService consultations;
    public AppointmentController(AppointmentService a, ConsultationService c) { appointments = a; consultations = c; }
    @GetMapping("/doctors") public List<DoctorResponse> doctors() { return appointments.doctors(); }
    @PostMapping("/appointments") public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody CreateAppointmentRequest r) { return ResponseEntity.status(HttpStatus.CREATED).body(appointments.book(r)); }
    @GetMapping("/appointments/today") public List<AppointmentResponse> today() { return appointments.today(); }
    @GetMapping("/appointments/{id}") public AppointmentResponse get(@PathVariable Long id) { return appointments.get(id); }
    @GetMapping("/appointments/{id}/consultation") public ConsultationResponse consultation(@PathVariable Long id) { return consultations.getForAppointment(id); }
    @PostMapping("/appointments/{id}/consultation") public ResponseEntity<ConsultationResponse> save(@PathVariable Long id, @Valid @RequestBody ConsultationRequest r) { return ResponseEntity.status(HttpStatus.CREATED).body(consultations.save(id, r)); }
    @PatchMapping("/appointments/{id}/complete") public ConsultationResponse complete(@PathVariable Long id) { return consultations.complete(id); }
}
