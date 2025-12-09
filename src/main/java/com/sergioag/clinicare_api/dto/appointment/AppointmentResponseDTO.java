package com.sergioag.clinicare_api.dto.appointment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sergioag.clinicare_api.enums.AppointmentStatus;
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
    private Long patientId;
    private String doctorName;
    private String specialityName;
    private LocalDateTime appointmentDate;
    private String appointmentType;
    private String reason;
    private AppointmentStatus status;
    private String doctorNotes;

    @JsonProperty("document_url")
    private String documentUrl;
}
