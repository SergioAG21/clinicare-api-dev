package com.sergioag.clinicare_api.repository;

import com.sergioag.clinicare_api.entity.Appointment;
import com.sergioag.clinicare_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    // Traer todas las citas de un paciente
    List<Appointment> findByPatientId(Long patientId);

    // Traer todas las citas de un doctor
    List<Appointment> findByDoctorId(Long doctorId);

    boolean existsByDoctorAndAppointmentDate(User doctor, LocalDateTime appointmentDate);

    List<Appointment> findBySpecialityId(Long specialityId);
}
