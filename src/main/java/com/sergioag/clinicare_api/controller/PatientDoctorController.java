package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.AssignRequestDTO;
import com.sergioag.clinicare_api.dto.UserResponseDTO;
import com.sergioag.clinicare_api.entity.User;
import com.sergioag.clinicare_api.mapper.UserMapper;
import com.sergioag.clinicare_api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient-doctor")
public class PatientDoctorController {
    private final UserService userService;
    private final UserMapper userMapper;

    public PatientDoctorController(UserService userService,  UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    // Asignar un paciente a un doctor
    @PostMapping("/assign")
    public ResponseEntity<?> assignPatientToDoctor(@RequestBody AssignRequestDTO request) {
        try {
            userService.assignPatientToDoctor(
                    request.getPatientId(),
                    request.getDoctorId()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Paciente asignado al doctor correctamente"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener los pacientes del doctor
    @GetMapping("/doctor/{doctorId}/patients")
    public ResponseEntity<List<UserResponseDTO>> getPatientsOfDoctor(@PathVariable Long doctorId) {
        User doctor = userService.findById(doctorId);

        if (doctor == null) return ResponseEntity.notFound().build();

        List<UserResponseDTO> patientsDTO = doctor.getPatients()
                .stream()
                .map(userMapper::toUserResponseDTO)
                .toList();

        return ResponseEntity.ok(patientsDTO);
    }

    // Obtener los doctores del paciente
    @GetMapping("/patient/{patientId}/doctors")
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
