package com.NetCracker.Controllers.Doctor;

import com.NetCracker.Domain.DTO.RequestRatingDto;
import com.NetCracker.Entities.Doctor.DoctorRating;
import com.NetCracker.Services.Doctor.DoctorRatingServiceImpl;
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
