package com.sergioag.clinicare_api.service;

import java.io.File;
import java.util.Map;

public interface EmailService {

    void sendEmail(String toUser, String subject, String templateName, Map<String, Object> variables);

    void sendEmailWithFile(String toUser, String subject, String message, File file);
}
