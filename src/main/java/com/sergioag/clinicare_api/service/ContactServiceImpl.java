package com.sergioag.clinicare_api.service;

import com.sergioag.clinicare_api.dto.email.EmailDTO;
import com.sergioag.clinicare_api.entity.ContactMessage;
import com.sergioag.clinicare_api.enums.MessageStatus;
import com.sergioag.clinicare_api.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ContactServiceImpl implements ContactService {
    private final ContactMessageRepository contactMessageRepository;
    private final EmailService emailService;

    public ContactServiceImpl(ContactMessageRepository contactMessageRepository, EmailService emailService) {
        this.contactMessageRepository = contactMessageRepository;
        this.emailService = emailService;
    }

    @Override
    public List<ContactMessage> findAll() {
        return contactMessageRepository.findAll();
    }

    @Override
    public Optional<ContactMessage> findById(Long id) {
        return this.contactMessageRepository.findById(id);
    }

    @Override
    public ContactMessage save(ContactMessage message) {
        return contactMessageRepository.save(message);
    }

    public ContactMessage update(Long id, String answer) {

        ContactMessage messageDB = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));

        messageDB.setAnswer(answer);
        messageDB.setStatus(MessageStatus.CLOSED);
        messageDB.getMessage();

        Map<String ,Object> variables = new HashMap<>();
        variables.put("pregunta", messageDB.getMessage());
        variables.put("respuesta", messageDB.getAnswer());
        variables.put("titulo", "Respuesta a tu consulta en CliniCare");
        variables.put("nombre", messageDB.getName());

        // Envío del email
        try {
            emailService.sendEmail(
                    messageDB.getEmail(),
                    "Respuesta a tu consulta en CliniCare",
                    "respuesta-consulta",
                    variables
            );
        } catch (Exception e) {
            System.err.println("Error al enviar email: " + e.getMessage());
        }

        return contactMessageRepository.save(messageDB);
    }


    @Override
    public void deleteById(Long id) {
        contactMessageRepository.deleteById(id);
    }

}
