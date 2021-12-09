package com.NetCracker.Controllers.Doctor;

import com.NetCracker.Domain.DTO.DoctorRatingProjection;
import com.NetCracker.Repositories.AllUserRepository;
import com.NetCracker.Repositories.Doctor.DoctorRatingRepository;
import com.NetCracker.Services.AllUserService;
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
