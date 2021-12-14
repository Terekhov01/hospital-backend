package com.NetCracker.controllers.doctor;

import com.NetCracker.domain.DTO.DoctorRatingProjection;
import com.NetCracker.repositories.AllUserRepository;
import com.NetCracker.repositories.doctor.DoctorRatingRepository;
import com.NetCracker.services.AllUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
//@CrossOrigin(origins = "http://localhost:4200")
//@RequestMapping({"/api"})
@RequestMapping({"/ourdoctors"})
public class AllUserController {

    private final AllUserService allUserService;
    private final AllUserRepository allUserRepository;
    private final DoctorRatingRepository doctorRatingRepository;

    @GetMapping
    public List<DoctorRatingProjection> findByRoleLike(String role) {
     return doctorRatingRepository.getRating();
    }
}
