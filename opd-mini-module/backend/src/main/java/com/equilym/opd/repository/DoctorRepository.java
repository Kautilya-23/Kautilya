package com.equilym.opd.repository;
import com.equilym.opd.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DoctorRepository extends JpaRepository<Doctor, Long> {}
