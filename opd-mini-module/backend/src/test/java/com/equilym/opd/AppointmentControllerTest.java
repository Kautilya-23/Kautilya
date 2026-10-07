package com.equilym.opd;

import com.equilym.opd.controller.AppointmentController;
import com.equilym.opd.dto.Dto.*;
import com.equilym.opd.exception.ConflictException;
import com.equilym.opd.exception.ResourceNotFoundException;
import com.equilym.opd.service.AppointmentService;
import com.equilym.opd.service.ConsultationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AppointmentController.class, properties = "app.cors.origin=http://localhost:4200")
class AppointmentControllerTest {
    @Autowired MockMvc mvc;
    @MockBean AppointmentService appointments;
    @MockBean ConsultationService consultations;

    static final String BOOKING = "{\"patientId\":1,\"doctorId\":1,\"appointmentDateTime\":\"2026-10-07T10:30:00\"}";
    static final String VITALS = "{\"bloodPressure\":\"120/80\",\"temperature\":98.6,\"notes\":\"Fever\"}";

    private ConsultationResponse consultation(boolean completed) {
        return new ConsultationResponse(1L, 1L, "Rahul Sharma", "Dr. Mehta", "120/80", 98.6, "Fever",
                LocalDateTime.now(), completed ? LocalDateTime.now() : null, completed);
    }

    @Test void bookReturns201() throws Exception {
        when(appointments.book(any())).thenReturn(new AppointmentResponse(1L, 1L, "Rahul Sharma", 1L, "Dr. Mehta", LocalDateTime.now(), "BOOKED"));
        mvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content(BOOKING))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("BOOKED"));
    }
    @Test void bookWithoutDoctorReturns400() throws Exception {
        mvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON)
                .content("{\"patientId\":1,\"appointmentDateTime\":\"2026-10-07T10:30:00\"}")).andExpect(status().isBadRequest());
    }
    @Test void bookForUnknownPatientReturns404() throws Exception {
        when(appointments.book(any())).thenThrow(new ResourceNotFoundException("Patient not found"));
        mvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content(BOOKING))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Patient not found"));
    }
    @Test void todayReturnsList() throws Exception {
        when(appointments.today()).thenReturn(List.of());
        mvc.perform(get("/api/appointments/today")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
    }
    @Test void unknownAppointmentReturns404() throws Exception {
        when(appointments.get(99L)).thenThrow(new ResourceNotFoundException("Appointment not found"));
        mvc.perform(get("/api/appointments/99")).andExpect(status().isNotFound()).andExpect(jsonPath("$.path").value("/api/appointments/99"));
    }
    @Test void saveConsultationReturns201() throws Exception {
        when(consultations.save(any(), any())).thenReturn(consultation(false));
        mvc.perform(post("/api/appointments/1/consultation").contentType(MediaType.APPLICATION_JSON).content(VITALS))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.completed").value(false));
    }
    @Test void invalidVitalsReturn400() throws Exception {
        mvc.perform(post("/api/appointments/1/consultation").contentType(MediaType.APPLICATION_JSON)
                .content("{\"bloodPressure\":\"abc\",\"temperature\":500,\"notes\":\"x\"}")).andExpect(status().isBadRequest());
    }
    @Test void completeReturns200() throws Exception {
        when(consultations.complete(1L)).thenReturn(consultation(true));
        mvc.perform(patch("/api/appointments/1/complete")).andExpect(status().isOk()).andExpect(jsonPath("$.completed").value(true));
    }
    @Test void duplicateCompletionReturns409() throws Exception {
        when(consultations.complete(1L)).thenThrow(new ConflictException("Consultation is already completed"));
        mvc.perform(patch("/api/appointments/1/complete")).andExpect(status().isConflict());
    }
}
