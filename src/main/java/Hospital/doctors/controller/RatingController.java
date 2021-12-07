package Hospital.doctors.controller;

import Hospital.doctors.domain.dto.RequestRatingDto;
import Hospital.doctors.domain.entity.DoctorRating;
import Hospital.doctors.service.DoctorRatingServiceImpl;
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
