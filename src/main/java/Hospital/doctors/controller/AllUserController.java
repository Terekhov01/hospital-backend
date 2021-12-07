package Hospital.doctors.controller;

import Hospital.doctors.domain.dto.DoctorRatingProjection;
import Hospital.doctors.domain.entity.User;
import Hospital.doctors.repository.AllUserRepository;
import Hospital.doctors.repository.DoctorRatingRepository;
import Hospital.doctors.service.AllUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
