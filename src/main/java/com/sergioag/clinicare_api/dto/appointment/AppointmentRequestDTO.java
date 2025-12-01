package com.sergioag.clinicare_api.dto.appointment;

import com.sergioag.clinicare_api.enums.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequestDTO {
    private Long doctorId;
    private Long speciality;
    private LocalDateTime appointmentDate;
    private String appointmentType;
    private String reason;
    private Long patientId;
    private AppointmentStatus status;
    private String doctorNotes;
}
