package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.specialty.SpecialtyDTO;
import com.sergioag.clinicare_api.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @GetMapping
    public ResponseEntity<List<SpecialtyDTO>> listAll() {
        return ResponseEntity.ok(specialtyService.findAllDTO());
    }
}
