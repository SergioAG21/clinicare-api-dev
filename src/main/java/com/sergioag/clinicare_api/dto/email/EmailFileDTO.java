package com.sergioag.clinicare_api.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailFileDTO {
    private String toUser;
    private String subject;
    private String message;
    MultipartFile file;
}
