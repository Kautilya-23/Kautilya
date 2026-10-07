package com.equilym.opd.controller;
import com.equilym.opd.dto.Dto.*;
import com.equilym.opd.service.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patients; private final ConsultationService consultations;
    public PatientController(PatientService p, ConsultationService c) { patients = p; consultations = c; }
    @PostMapping public ResponseEntity<PatientResponse> create(@Valid @RequestBody CreatePatientRequest r) { return ResponseEntity.status(HttpStatus.CREATED).body(patients.create(r)); }
    @GetMapping public List<PatientResponse> list(@RequestParam(required = false) String q) { return patients.find(q); }
    @GetMapping("/{id}/consultations") public List<ConsultationResponse> history(@PathVariable Long id) { return consultations.history(id); }
}
