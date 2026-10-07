package com.equilym.opd.service;
import com.equilym.opd.dto.Dto.*;
import com.equilym.opd.entity.Patient;
import com.equilym.opd.exception.ConflictException;
import com.equilym.opd.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class PatientService {
    private final PatientRepository repo;
    public PatientService(PatientRepository repo) { this.repo = repo; }
    @Transactional
    public PatientResponse create(CreatePatientRequest r) {
        if (repo.existsByPhoneNumber(r.phoneNumber())) throw new ConflictException("A patient with this phone number already exists");
        Patient p = new Patient();
        p.setName(r.name().trim()); p.setGender(r.gender()); p.setAge(r.age()); p.setPhoneNumber(r.phoneNumber());
        return toDto(repo.save(p));
    }
    @Transactional(readOnly = true)
    public List<PatientResponse> find(String q) {
        List<Patient> list = (q == null || q.isBlank()) ? repo.findAll() : repo.findByNameContainingIgnoreCaseOrPhoneNumberContaining(q.trim(), q.trim());
        return list.stream().map(PatientService::toDto).toList();
    }
    static PatientResponse toDto(Patient p) { return new PatientResponse(p.getId(), p.getName(), p.getGender(), p.getAge(), p.getPhoneNumber()); }
}
