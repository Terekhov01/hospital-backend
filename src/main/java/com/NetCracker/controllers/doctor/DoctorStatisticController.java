package com.NetCracker.controllers.doctor;


import com.NetCracker.domain.projection.DoctorEmploymentProjection;
import com.NetCracker.repositories.appointment.AppointmentRegistrationRepo;
import com.NetCracker.repositories.doctor.DoctorRepository;
import com.NetCracker.services.doctor.DoctorStatisticService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping({"/doctorstatistic"})
public class DoctorStatisticController {

    @Autowired
    DoctorStatisticService doctorStatisticService;
    @Autowired
    AppointmentRegistrationRepo appointmentRegistrationRepo;
    @Autowired
    DoctorRepository doctorRepository;

    @PreAuthorize("permitAll()")
    @GetMapping(path = {"/{id}"})
    public Object notcreate(@PathVariable("id") Long doctor) throws Exception {
        System.out.println("get");
        return appointmentRegistrationRepo.search(doctor);
    }


    @GetMapping()
    public List<DoctorEmploymentProjection> findAll() {
        System.out.println(doctorRepository.employment());
        return doctorRepository.employment();
    }


}
