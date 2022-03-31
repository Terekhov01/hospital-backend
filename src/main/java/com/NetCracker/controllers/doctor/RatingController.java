package com.NetCracker.controllers.doctor;

import com.NetCracker.domain.DTO.RequestRatingDto;
import com.NetCracker.entities.doctor.DoctorRating;
import com.NetCracker.services.doctor.DoctorRatingServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
//@CrossOrigin(origins = "*", maxAge = 3600)
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping({"/rating"})
public class RatingController {

    private final DoctorRatingServiceImpl doctorRatingService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    // Доступ к созданию рейтингов для врачей
    @PostMapping
    public DoctorRating createRating(@RequestBody RequestRatingDto requestRatingDto){
        System.out.println();
        return doctorRatingService.createRating(requestRatingDto);
    }


}
