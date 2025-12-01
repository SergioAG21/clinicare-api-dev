package com.sergioag.clinicare_api.service;

import com.sergioag.clinicare_api.dto.specialty.SpecialityCountDTO;
import com.sergioag.clinicare_api.dto.specialty.SpecialtyDTO;
import com.sergioag.clinicare_api.entity.Specialty;

import java.util.List;

public interface SpecialtyService {
    List<Specialty> findAll();
    List<SpecialtyDTO> findAllDTO();

    List<SpecialityCountDTO> getTop6Specialities();
}
