package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.ContactMessageResponseDTO;
import com.sergioag.clinicare_api.entity.ContactMessage;
import com.sergioag.clinicare_api.mapper.ContactMessageMapper;
import com.sergioag.clinicare_api.service.ContactService;
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
public class ContactMessageController {
    private final ContactService contactService;
    private final ContactMessageMapper contactMessageMapper;

    public ContactMessageController(ContactService contactService, ContactMessageMapper contactMessageMapper) {
        this.contactService = contactService;
        this.contactMessageMapper = contactMessageMapper;
    }

    @GetMapping
    public ResponseEntity<List<ContactMessageResponseDTO>> findAll() {
        List<ContactMessage> messages = contactService.findAll();
        List<ContactMessageResponseDTO> contactMessage = contactMessageMapper.toMessageResponseDTOs(messages);

        return ResponseEntity.ok(contactMessage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactMessageResponseDTO> findById(@PathVariable Long id) {
        Optional<ContactMessage> message = contactService.findById(id);

        if (message.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ContactMessageResponseDTO dto = contactMessageMapper.toMessageResponseDTO(message.get());

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<ContactMessage> contactMessageOptional = contactService.findById(id);

        if(!contactMessageOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        contactService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/send")
    public ResponseEntity<?> save(@RequestBody ContactMessage message, BindingResult result) {
        if(result.hasErrors()) {
            return validation(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(contactService.save(message));
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> {
            errors.put(error.getField(), "En el campo " + error.getField() + " " + error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
