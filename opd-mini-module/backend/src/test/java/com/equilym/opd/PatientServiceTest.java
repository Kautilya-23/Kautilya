package com.equilym.opd;
import com.equilym.opd.dto.Dto.*;
import com.equilym.opd.entity.Patient;
import com.equilym.opd.exception.ConflictException;
import com.equilym.opd.repository.PatientRepository;
import com.equilym.opd.service.PatientService;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
class PatientServiceTest {
    PatientRepository repo = mock(PatientRepository.class);
    PatientService service = new PatientService(repo);
    CreatePatientRequest req = new CreatePatientRequest("Rahul Sharma", "MALE", 30, "9876543210");

    @Test void createsPatient() {
        when(repo.existsByPhoneNumber("9876543210")).thenReturn(false);
        when(repo.save(any(Patient.class))).thenAnswer(i -> i.getArgument(0));
        PatientResponse r = service.create(req);
        assertEquals("Rahul Sharma", r.name());
    }
    @Test void duplicatePhoneThrowsConflict() {
        when(repo.existsByPhoneNumber("9876543210")).thenReturn(true);
        assertThrows(ConflictException.class, () -> service.create(req));
        verify(repo, never()).save(any());
    }
    @Test void blankQueryListsAll() { when(repo.findAll()).thenReturn(List.of(new Patient())); assertEquals(1, service.find(" ").size()); }
    @Test void searchUsesNameOrPhone() {
        when(repo.findByNameContainingIgnoreCaseOrPhoneNumberContaining("rahul", "rahul")).thenReturn(List.of(new Patient()));
        assertEquals(1, service.find("rahul").size());
    }
}
