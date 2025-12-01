package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.appointment.AppointmentRequestDTO;
import com.sergioag.clinicare_api.dto.appointment.AppointmentResponseDTO;
import com.sergioag.clinicare_api.mapper.AppointmentMapper;
import com.sergioag.clinicare_api.repository.AppointmentRepository;
import com.sergioag.clinicare_api.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;

    AppointmentController(AppointmentService appointmentService, AppointmentMapper appointmentMapper, AppointmentRepository appointmentRepository) {
        this.appointmentService = appointmentService;
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequestDTO dto) {
        try {
            AppointmentResponseDTO appointment = appointmentService.saveAppointment(dto);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException ex) {
            // Aquí capturas el error si, por ejemplo, el doctor ya tiene cita
            return ResponseEntity
                    .badRequest() // HTTP 400
                    .body(Map.of("error", ex.getMessage())); // mensaje que quieres enviar al front
        }
    }

    @GetMapping
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponseDTO)
                .toList();
    }


    @GetMapping("/{id}")
    public AppointmentResponseDTO getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @GetMapping("/speciality/{id}")
    public List<AppointmentResponseDTO> getAppointmentBySpecialityId(@PathVariable Long id) {
        return appointmentService.getAppointmentsBySpecialityId(id);
    }

    @GetMapping("/doctor/{id}")
    public List<AppointmentResponseDTO> getAppointmentByDoctorId(@PathVariable Long id) {
        return appointmentService.getAppointmentsByDoctorId(id);
    }

    @GetMapping("/patient/{id}")
    public List<AppointmentResponseDTO> getAppointmentByPatientId(@PathVariable Long id) {
        return appointmentService.getAppointmentsByPatientId(id);
    }

}
