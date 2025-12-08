package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.appointment.AppointmentRequestDTO;
import com.sergioag.clinicare_api.dto.appointment.AppointmentResponseDTO;
import com.sergioag.clinicare_api.entity.Appointment;
import com.sergioag.clinicare_api.mapper.AppointmentMapper;
import com.sergioag.clinicare_api.repository.AppointmentRepository;
import com.sergioag.clinicare_api.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointment")
@Tag(name = "Citas", description = "Gestión de las Citas")
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
    @Operation(summary = "Crea una cita con todos los detalles")
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequestDTO dto) {
        try {
            AppointmentResponseDTO appointment = appointmentService.saveAppointment(dto);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todas las Citas")
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponseDTO)
                .toList();
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una cita por su ID")
    public AppointmentResponseDTO getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @PutMapping("/cancel/{id}")
    @Operation(summary = "Cancelar una cita por ID", description = "Cambia el estado de la cita a Cancelado")
    public Appointment cancelAppointment(@PathVariable Long id) {
        return appointmentService.cancelAppointment(id);
    }

    @PutMapping("/notes/{id}")
    @Operation(summary = "Agregar notas del Doctor por ID de la Cita")
    public ResponseEntity<?> addDoctorNotes(@PathVariable Long id, @RequestBody Map<String, String> body, BindingResult result) {

        String doctorNotes = body.get("doctorNotes");

        if(result.hasErrors()) {
            return validation(result);
        }

        Appointment updated = appointmentService.addDoctorNotes(id, doctorNotes);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/upload-document")
    @Operation(summary = "Subir un documento PDF")
    public ResponseEntity<?> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            if (!file.getContentType().equals("application/pdf")) {
                return ResponseEntity.badRequest().body("File must be a PDF");
            }

            String fileName = id + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path uploadDir = Paths.get("uploads/appointments");
            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());

            String publicUrl = "http://localhost:8080/uploads/appointments/" + fileName;

            Appointment appointment = appointmentRepository.findById(id).orElseThrow();
            appointment.setDocumentUrl(publicUrl);  // crea este campo
            appointmentRepository.save(appointment);

            return ResponseEntity.ok(Map.of(
                    "message", "Documento PDF subido correctamente",
                    "url", publicUrl
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir el documento");
        }
    }


    @GetMapping("/speciality/{id}")
    @Operation(summary = "Obtiene Citas por Especialidad", description = "Indicando el ID de la Especialidad obtiene las Citas")
    public List<AppointmentResponseDTO> getAppointmentBySpecialityId(@PathVariable Long id) {
        return appointmentService.getAppointmentsBySpecialityId(id);
    }

    @GetMapping("/doctor/{id}")
    @Operation(summary = "Obtiene Citas por Doctor", description = "Indicando el ID del Doctor obtiene sus Citas")
    public List<AppointmentResponseDTO> getAppointmentByDoctorId(@PathVariable Long id) {
        return appointmentService.getAppointmentsByDoctorId(id);
    }

    @GetMapping("/patient/{id}")
    @Operation(summary = "Obtiene Citas por Paciente", description = "Indicando el ID del Paciente obtiene sus Citas")
    public List<AppointmentResponseDTO> getAppointmentByPatientId(@PathVariable Long id) {
        return appointmentService.getAppointmentsByPatientId(id);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> {
            errors.put(error.getField(), "En el campo " + error.getField() + " " + error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
