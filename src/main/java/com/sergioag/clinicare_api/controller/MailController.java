package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.email.EmailDTO;
import com.sergioag.clinicare_api.dto.email.EmailFileDTO;
import com.sergioag.clinicare_api.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class MailController {

    private final EmailService emailService;

    public MailController(EmailService emailService) {
        this.emailService = emailService;
    }

//    @PostMapping("/send-message")
//    public ResponseEntity<?> receiveRequestEmail(@RequestBody EmailDTO emailDTO) {
//        emailService.sendEmail(emailDTO.getToUser(), emailDTO.getSubject(), emailDTO.getMessage());
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/send-message-file")
    public ResponseEntity<?> receiveRequestEmailWithFile(@ModelAttribute EmailFileDTO emailFileDTO) {

        try {
            String fileName = emailFileDTO.getFile().getOriginalFilename();

            Path path = Paths.get("src/main/resources/files/informes/" + fileName);

            Files.createDirectories(path.getParent());

            Files.copy(emailFileDTO.getFile().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            File file = path.toFile();

            emailService.sendEmailWithFile(emailFileDTO.getToUser(), emailFileDTO.getSubject(), emailFileDTO.getMessage(), file);

            return ResponseEntity.ok().build();
        } catch(Exception e) {
            throw new RuntimeException("Error al enviar el email con el archivo. " + e.getMessage());
        }
    }
}
