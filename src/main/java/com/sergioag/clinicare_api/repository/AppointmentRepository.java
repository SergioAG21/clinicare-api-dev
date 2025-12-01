package com.sergioag.clinicare_api.repository;

import com.sergioag.clinicare_api.dto.specialty.SpecialityCountDTO;
import com.sergioag.clinicare_api.entity.Appointment;
import com.sergioag.clinicare_api.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    // Traer todas las citas de un paciente
    List<Appointment> findByPatientId(Long patientId);

    // Traer todas las citas de un doctor
    List<Appointment> findByDoctorId(Long doctorId);

    boolean existsByDoctorAndAppointmentDate(User doctor, LocalDateTime appointmentDate);

    List<Appointment> findBySpecialityId(Long specialityId);

    @Query("""
           SELECT new com.sergioag.clinicare_api.dto.specialty.SpecialityCountDTO(
               s.name,
               COUNT(a.id)
           )
           FROM Appointment a
           JOIN a.speciality s
           GROUP BY s.id, s.name
           ORDER BY COUNT(a.id) DESC
           """)
    List<SpecialityCountDTO> getAppointmentsCountBySpeciality(Pageable pageable);
}
