package com.sergioag.clinicare_api.service;

import com.sergioag.clinicare_api.dto.appointment.AppointmentRequestDTO;
import com.sergioag.clinicare_api.dto.appointment.AppointmentResponseDTO;
import com.sergioag.clinicare_api.entity.Appointment;
import com.sergioag.clinicare_api.entity.User;
import com.sergioag.clinicare_api.entity.UserRole;
import com.sergioag.clinicare_api.enums.AppointmentStatus;
import com.sergioag.clinicare_api.enums.AppointmentType;
import com.sergioag.clinicare_api.mapper.AppointmentMapper;
import com.sergioag.clinicare_api.repository.AppointmentRepository;
import com.sergioag.clinicare_api.repository.UserRepository;
import com.sergioag.clinicare_api.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final AppointmentMapper appointmentMapper;
    private final UserRoleRepository userRoleRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  UserRepository userRepository,
                                  AppointmentMapper appointmentMapper,
                                  UserRoleRepository userRoleRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.appointmentMapper = appointmentMapper;
        this.userRoleRepository = userRoleRepository;
    }

    // Crear una cita
    @Override
    public AppointmentResponseDTO saveAppointment(AppointmentRequestDTO dto) {
        // Obtener entidades relacionadas
        User patient = userRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDateTime dateTime = dto.getAppointmentDate();

        boolean exists = appointmentRepository.existsByDoctorAndAppointmentDate(doctor, dateTime);
        if (exists) {
            throw new RuntimeException("El doctor selccionado ya tiene una cita a esa hora");
        }

        UserRole doctorRole = userRoleRepository.findByUserIdAndRoleName(doctor.getId(), "DOCTOR")
                .orElseThrow(() -> new RuntimeException("Doctor role not found"));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSpeciality(doctorRole.getSpecialty());
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setAppointmentType(AppointmentType.valueOf(dto.getAppointmentType()));
        appointment.setReason(dto.getReason());
        appointment.setStatus(AppointmentStatus.PENDING);

        Appointment saved = appointmentRepository.save(appointment);

        return appointmentMapper.toResponseDTO(saved);
    }


    // Obtener Todas las citas
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentMapper.toResponseDTOs(appointmentRepository.findAll());
    }

    // Obtener Todas las citas de cada paciente
    public List<AppointmentResponseDTO> getAppointmentsByPatientId(Long patientId) {
        return appointmentMapper.toResponseDTOs(
                appointmentRepository.findByPatientId(patientId)
        );
    }

    // Obtener todas las citas de cada doctor
    public List<AppointmentResponseDTO> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentMapper.toResponseDTOs(
                appointmentRepository.findByDoctorId(doctorId)
        );
    }

    // Obtener una cita individual por ID
    public AppointmentResponseDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Appointment not found with id: " + id));
        return appointmentMapper.toResponseDTO(appointment);
    }

    // Obtener las citas po ID de la especialidad
    public List<AppointmentResponseDTO> getAppointmentsBySpecialityId(Long specialityId) {
        return appointmentMapper.toResponseDTOs(
                appointmentRepository.findBySpecialityId(specialityId)
        );
    }

    // Cancelar la cita
    @Override
    public Appointment cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CANCELLED);

        return appointmentRepository.save(appointment);
    }

    public Appointment addDoctorNotes(Long appointmentId, String notes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setDoctorNotes(notes);
        appointment.setStatus(AppointmentStatus.COMPLETED);

        return appointmentRepository.save(appointment);
    }

}
