package com.equilym.opd.config;
import com.equilym.opd.entity.Doctor;
import com.equilym.opd.repository.DoctorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class DataSeeder implements CommandLineRunner {
    private final DoctorRepository doctors;
    public DataSeeder(DoctorRepository doctors) { this.doctors = doctors; }
    @Override public void run(String... args) {
        if (doctors.count() == 0) doctors.saveAll(List.of(
            new Doctor("Dr. Mehta", "General Medicine"), new Doctor("Dr. Iyer", "Pediatrics"), new Doctor("Dr. Khan", "Orthopedics")));
    }
}
