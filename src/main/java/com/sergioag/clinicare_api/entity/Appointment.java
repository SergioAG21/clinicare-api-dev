package com.sergioag.clinicare_api.entity;

import com.sergioag.clinicare_api.enums.AppointmentStatus;
import com.sergioag.clinicare_api.enums.AppointmentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "appointments",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_doctor_datetime",
                columnNames = {"doctor_id", "appointment_date"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"doctor", "patient"})
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime appointmentDate;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "speciality_id", nullable = false)
    private Specialty speciality;

    @Enumerated(EnumType.STRING)
    private AppointmentType appointmentType;

    private String reason;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private String doctorNotes;

    @Column(name = "document_url")
    private String documentUrl;
}
