package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.UserResponseDTO;
import com.sergioag.clinicare_api.entity.User;
import com.sergioag.clinicare_api.mapper.UserMapper;
import com.sergioag.clinicare_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient-doctor")
@Tag(name = "Relación entre Doctor y Paciente", description = "Gestión la asignación de los usuarios")
public class PatientDoctorController {
    private final UserService userService;
    private final UserMapper userMapper;

    public PatientDoctorController(UserService userService,  UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/doctor/{doctorId}/patients")
    @Operation(summary = "Obtener los pacientes del doctor")
    public ResponseEntity<List<UserResponseDTO>> getPatientsOfDoctor(@PathVariable Long doctorId) {
        User doctor = userService.findById(doctorId);

        if (doctor == null) return ResponseEntity.notFound().build();

        List<UserResponseDTO> patientsDTO = doctor.getPatients()
                .stream()
                .map(userMapper::toUserResponseDTO)
                .toList();

        return ResponseEntity.ok(patientsDTO);
    }

    @GetMapping("/speciality/{specialityId}/doctors")
    @Operation(summary = "Obtiene los Doctores por Especialidad")
    public ResponseEntity<List<UserResponseDTO>> getDoctorsBySpeciality(@PathVariable Long specialityId) {

        List<User> doctors = userService.findDoctorBySpecialtyId(specialityId);

        if (doctors.isEmpty()) return ResponseEntity.noContent().build();

        List<UserResponseDTO> doctorDTOs = doctors.stream()
                .map(userMapper::toUserResponseDTO)
                .toList();

        return ResponseEntity.ok(doctorDTOs);
    }

    @GetMapping("/patient/{patientId}/doctors")
    @Operation(summary = "Obtener los doctores del paciente")
    public ResponseEntity<List<UserResponseDTO>> getDoctorsOfPatient(@PathVariable Long patientId) {
        User patient = userService.findById(patientId);

        if (patient == null) return ResponseEntity.notFound().build();

        List<UserResponseDTO> doctorsDTO = patient.getDoctors()
                .stream()
                .map(userMapper::toUserResponseDTO)
                .toList();

        return ResponseEntity.ok(doctorsDTO);
    }
}
