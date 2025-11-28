package com.sergioag.clinicare_api.service;

import com.sergioag.clinicare_api.dto.appointment.AppointmentRequestDTO;
import com.sergioag.clinicare_api.dto.appointment.AppointmentResponseDTO;
import com.sergioag.clinicare_api.entity.Appointment;

import java.util.List;

public interface AppointmentService {
    AppointmentResponseDTO saveAppointment(AppointmentRequestDTO appointment);

    List<AppointmentResponseDTO> getAllAppointments();

    List<AppointmentResponseDTO> getAppointmentsByPatientId(Long patientId);

    List<AppointmentResponseDTO> getAppointmentsByDoctorId(Long doctorId);

    AppointmentResponseDTO getAppointmentById(Long id);
}
