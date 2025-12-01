package com.sergioag.clinicare_api.dto.specialty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpecialityCountDTO {
    private String name;
    private Long total;
}
