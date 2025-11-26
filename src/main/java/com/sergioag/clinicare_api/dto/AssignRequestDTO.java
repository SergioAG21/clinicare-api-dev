package com.sergioag.clinicare_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignRequestDTO {
    private Long patientId;
    private Long doctorId;
}
