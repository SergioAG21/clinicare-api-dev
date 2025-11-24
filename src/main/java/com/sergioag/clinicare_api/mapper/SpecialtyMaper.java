package com.sergioag.clinicare_api.mapper;

import com.sergioag.clinicare_api.dto.specialty.SpecialtyDTO;
import com.sergioag.clinicare_api.entity.Specialty;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpecialtyMaper {

    List<SpecialtyDTO> toSpecialtyDTO(List<Specialty> specialties);
}
