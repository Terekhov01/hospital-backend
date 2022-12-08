package com.NetCracker.controllers.doctor;

import com.NetCracker.domain.projection.DoctorRatingProjection;
//import com.NetCracker.repositories.AllUserRepository;
import com.NetCracker.repositories.doctor.DoctorRatingRepository;
import com.NetCracker.repositories.doctor.DoctorRepository;
//import com.NetCracker.services.AllUserService;
import com.NetCracker.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
//@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
//@CrossOrigin(origins = "http://localhost:4200")
//@RequestMapping({"/api"})
@RequestMapping({"/ourdoctors"})
public class AllUserController {

//    private final AllUserService allUserService;
//    private final AllUserRepository allUserRepository;
    private final DoctorRatingRepository doctorRatingRepository;

    private final DoctorRepository doctorRepository;
    private final UserService userService;

    @PreAuthorize("permitAll()")
    @GetMapping
    public List<DoctorRatingProjection> findByRoleLike(Long id) {

     return doctorRatingRepository.getRating();
    }
}
