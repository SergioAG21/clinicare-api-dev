package com.sergioag.clinicare_api.controller;

import com.sergioag.clinicare_api.dto.specialty.SpecialityCountDTO;
import com.sergioag.clinicare_api.repository.AppointmentRepository;
import com.sergioag.clinicare_api.repository.SpecialtyRepository;
import com.sergioag.clinicare_api.service.SpecialtyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@Tag(name = "Estadísticas")
public class StatsController {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyService specialtyService;
    private final AppointmentRepository appointmentRepository;

    public StatsController(SpecialtyRepository specialtyRepository, AppointmentRepository appointmentRepository, SpecialtyService specialtyService) {
        this.specialtyRepository = specialtyRepository;
        this.appointmentRepository = appointmentRepository;
        this.specialtyService = specialtyService;
    }

    @GetMapping("/top-specialities")
    @Operation(summary = "Obtiene las 6 especialidades con más Citas")
    public List<SpecialityCountDTO> getTopSpecialities() {
        return specialtyService.getTop6Specialities();
    }


}
