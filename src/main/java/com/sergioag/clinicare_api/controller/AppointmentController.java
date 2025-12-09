package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.appointment.AppointmentRequestDTO;
import com.sergioag.clinicare_api.dto.appointment.AppointmentResponseDTO;
import com.sergioag.clinicare_api.entity.Appointment;
import com.sergioag.clinicare_api.mapper.AppointmentMapper;
import com.sergioag.clinicare_api.repository.AppointmentRepository;
import com.sergioag.clinicare_api.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
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
    @Operation(summary = "Subir un documento PDF o imagen")
    public ResponseEntity<?> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo está vacío");
            }

            // Tipos permitidos
            List<String> allowedTypes = List.of(
                    "application/pdf",
                    "image/png",
                    "image/jpeg",
                    "image/jpg",
                    "image/webp"
            );

            String mimeType = file.getContentType();

            if (mimeType == null || !allowedTypes.contains(mimeType)) {
                return ResponseEntity.badRequest().body(
                        "Formato no permitido. Solo se aceptan PDF o imágenes (PNG, JPG, JPEG, WEBP)"
                );
            }

            // Mantener nombre original pero evitar problemas
            String cleanOriginalName = file.getOriginalFilename().replace(" ", "_");

            String fileName = id + "_" + System.currentTimeMillis() + "_" + cleanOriginalName;

            Path uploadDir = Paths.get("uploads/appointments");
            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());

            // URL pública accesible desde el frontend
            String publicUrl = "http://localhost:8080/uploads/appointments/" + fileName;

            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

            appointment.setDocumentUrl(publicUrl);
            appointmentRepository.save(appointment);

            return ResponseEntity.ok(Map.of(
                    "message", "Archivo subido correctamente",
                    "url", publicUrl
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir el documento: " + e.getMessage());
        }
    }


    @GetMapping("{id}/download-document")
    @Operation(summary = "Descargar el documento adjunto de una cita")
    public ResponseEntity<?> downloadDocument(@PathVariable Long id) {

        try {
            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

            if (appointment.getDocumentUrl() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Esta cita no tiene documento adjunto");
            }

            String documentUrl = appointment.getDocumentUrl();

            // EXTRAER NOMBRE DEL ARCHIVO SIN URI (evita errores por espacios)
            String fileName = documentUrl.substring(documentUrl.lastIndexOf('/') + 1);

            Path filePath = Paths.get("uploads/appointments").resolve(fileName);

            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El archivo no existe en el servidor");
            }

            // Detectar tipo MIME automáticamente
            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream"; // genérico por si no detecta
            }

            byte[] fileBytes = Files.readAllBytes(filePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(fileBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al descargar el documento: " + e.getMessage());
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
