package com.NetCracker.controllers.doctor;

import com.NetCracker.domain.DTO.RequestRatingDto;
import com.NetCracker.entities.doctor.DoctorRating;
import com.NetCracker.services.doctor.DoctorRatingServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping({"/rating"})
public class RatingController {

    private final DoctorRatingServiceImpl doctorRatingService;

    @PostMapping
    public DoctorRating createRating(@RequestBody RequestRatingDto requestRatingDto){
        return doctorRatingService.createRating(requestRatingDto);
    }


}
