package com.sergioag.clinicare_api.mapper;

import com.sergioag.clinicare_api.dto.appointment.AppointmentResponseDTO;
import com.sergioag.clinicare_api.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(source = "patient", target = "patientName", qualifiedByName = "fullPatientName")
    @Mapping(source = "doctor", target = "doctorName", qualifiedByName = "fullDoctorName")
    @Mapping(source = "speciality.name", target = "specialityName")
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "documentUrl", target = "documentUrl")
    AppointmentResponseDTO toResponseDTO(Appointment appointment);

    List<AppointmentResponseDTO> toResponseDTOs(List<Appointment> appointments);

    @Named("fullPatientName")
    default String fullPatientName(com.sergioag.clinicare_api.entity.User patient) {
        if (patient == null) return "";
        return patient.getName() + " " + patient.getLastName();
    }

    @Named("fullDoctorName")
    default String fullDoctorName(com.sergioag.clinicare_api.entity.User doctor) {
        if (doctor == null) return "";
        return doctor.getName() + " " + doctor.getLastName();
    }
}
