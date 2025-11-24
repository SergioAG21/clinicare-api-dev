package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.specialty.SpecialtyDTO;
import com.sergioag.clinicare_api.entity.Specialty;
import com.sergioag.clinicare_api.mapper.SpecialtyMaper;
import com.sergioag.clinicare_api.service.SpecialtyService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/specialities")
public class SpecialtyController {

    private final SpecialtyService specialtyService;
    private final SpecialtyMaper specialtyMaper;

    public SpecialtyController(SpecialtyService specialtyService, SpecialtyMaper specialtyMaper) {
        this.specialtyService = specialtyService;
        this.specialtyMaper = specialtyMaper;
    }

    @Transactional
    @GetMapping
    public ResponseEntity<List<SpecialtyDTO>> listAll() {
        List<Specialty> specialities = specialtyService.findAll();

        return ResponseEntity.ok((specialtyMaper.toSpecialtyDTO(specialities)));
    }
}
