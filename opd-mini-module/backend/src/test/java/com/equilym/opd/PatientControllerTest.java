package com.equilym.opd;
import com.equilym.opd.controller.PatientController;
import com.equilym.opd.dto.Dto.PatientResponse;
import com.equilym.opd.exception.ConflictException;
import com.equilym.opd.exception.ResourceNotFoundException;
import com.equilym.opd.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(value = PatientController.class, properties = "app.cors.origin=http://localhost:4200")
class PatientControllerTest {
    @Autowired MockMvc mvc;
    @MockBean PatientService patients;
    @MockBean ConsultationService consultations;
    static final String VALID = "{\"name\":\"Rahul Sharma\",\"gender\":\"MALE\",\"age\":30,\"phoneNumber\":\"9876543210\"}";

    @Test void createReturns201() throws Exception {
        when(patients.create(any())).thenReturn(new PatientResponse(1L, "Rahul Sharma", "MALE", 30, "9876543210"));
        mvc.perform(post("/api/patients").contentType(MediaType.APPLICATION_JSON).content(VALID)).andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
    }
    @Test void invalidPatientReturns400() throws Exception {
        mvc.perform(post("/api/patients").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\",\"gender\":\"X\",\"age\":500,\"phoneNumber\":\"12\"}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }
    @Test void duplicateReturns409() throws Exception {
        when(patients.create(any())).thenThrow(new ConflictException("A patient with this phone number already exists"));
        mvc.perform(post("/api/patients").contentType(MediaType.APPLICATION_JSON).content(VALID)).andExpect(status().isConflict());
    }
    @Test void searchPassesQueryToService() throws Exception {
        when(patients.find("rahul")).thenReturn(List.of(new PatientResponse(1L, "Rahul Sharma", "MALE", 30, "9876543210")));
        mvc.perform(get("/api/patients").param("q", "rahul")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Rahul Sharma"));
    }
    @Test void historyForUnknownPatientReturns404() throws Exception {
        when(consultations.history(9L)).thenThrow(new ResourceNotFoundException("Patient not found"));
        mvc.perform(get("/api/patients/9/consultations")).andExpect(status().isNotFound());
    }
}
