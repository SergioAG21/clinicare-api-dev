package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.ContactMessageResponseDTO;
import com.sergioag.clinicare_api.dto.email.EmailDTO;
import com.sergioag.clinicare_api.entity.ContactMessage;
import com.sergioag.clinicare_api.mapper.ContactMessageMapper;
import com.sergioag.clinicare_api.service.ContactService;
import com.sergioag.clinicare_api.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/contact")
@Tag(name = "Mensajes de Contacto", description = "Gestión de los mensajes de Contacto")
public class ContactMessageController {
    private final ContactService contactService;
    private final EmailService emailService;
    private final ContactMessageMapper contactMessageMapper;

    public ContactMessageController(ContactService contactService, ContactMessageMapper contactMessageMapper, EmailService emailService) {
        this.contactService = contactService;
        this.contactMessageMapper = contactMessageMapper;
        this.emailService = emailService;
    }

    @GetMapping
    @Operation(summary = "Obtiene todos los mensajes")
    public ResponseEntity<List<ContactMessageResponseDTO>> findAll() {
        List<ContactMessage> messages = contactService.findAll();
        List<ContactMessageResponseDTO> contactMessage = contactMessageMapper.toMessageResponseDTOs(messages);

        return ResponseEntity.ok(contactMessage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene detalles del mensaje por ID")
    public ResponseEntity<ContactMessageResponseDTO> findById(@PathVariable Long id) {
        Optional<ContactMessage> message = contactService.findById(id);

        if (message.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ContactMessageResponseDTO dto = contactMessageMapper.toMessageResponseDTO(message.get());

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina mensaje por ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<ContactMessage> contactMessageOptional = contactService.findById(id);

        if(!contactMessageOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        contactService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/send")
    @Operation(summary = "Guarda el mensaje")
    public ResponseEntity<?> save(@RequestBody ContactMessage message, BindingResult result) {
        if(result.hasErrors()) {
            return validation(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(contactService.save(message));
    }

    @PostMapping("/answer/{id}")
    @Operation(summary = "Respuesta del mensaje por ID")
    public ResponseEntity<?> answer(@PathVariable Long id, @RequestBody Map<String, String> body, BindingResult result) {

        String answer = body.get("answer");

        if(result.hasErrors()) {
            return validation(result);
        }

        ContactMessage updated = contactService.update(id, answer);

        return ResponseEntity.ok(updated);
    }


    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> {
            errors.put(error.getField(), "En el campo " + error.getField() + " " + error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
