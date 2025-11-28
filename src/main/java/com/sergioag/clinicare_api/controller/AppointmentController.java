package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.appointment.AppointmentRequestDTO;
import com.sergioag.clinicare_api.dto.appointment.AppointmentResponseDTO;
import com.sergioag.clinicare_api.entity.Appointment;
import com.sergioag.clinicare_api.mapper.AppointmentMapper;
import com.sergioag.clinicare_api.repository.AppointmentRepository;
import com.sergioag.clinicare_api.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    AppointmentController(AppointmentService appointmentService, AppointmentMapper appointmentMapper) {
        this.appointmentService = appointmentService;
        this.appointmentMapper = appointmentMapper;
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


}
