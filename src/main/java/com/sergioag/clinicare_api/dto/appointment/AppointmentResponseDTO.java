package com.sergioag.clinicare_api.dto.appointment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponseDTO {
    private Long id;
    private String patientName;
    private String doctorName;
    private String specialityName;
    private LocalDateTime appointmentDate;
    private String appointmentType;
    private String reason;
}
