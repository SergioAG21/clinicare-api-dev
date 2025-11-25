package com.sergioag.clinicare_api.service.impl;

import com.sergioag.clinicare_api.dto.specialty.SpecialtyDTO;
import com.sergioag.clinicare_api.entity.Specialty;
import com.sergioag.clinicare_api.mapper.SpecialtyMapper;
import com.sergioag.clinicare_api.repository.SpecialtyRepository;
import com.sergioag.clinicare_api.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    @Override
    public List<Specialty> findAll() {
        return specialtyRepository.findAll();
    }

    @Override
    public List<SpecialtyDTO> findAllDTO() {
        List<Specialty> specialties = specialtyRepository.findAll();
        return specialtyMapper.toSpecialtyDTO(specialties);
    }
}
