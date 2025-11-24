package com.sergioag.clinicare_api.service;

import com.sergioag.clinicare_api.entity.Specialty;
import com.sergioag.clinicare_api.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {
    private final SpecialtyRepository specialtyRepository;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    @Transactional
    public List<Specialty> findAll() { return specialtyRepository.findAll(); }
}
