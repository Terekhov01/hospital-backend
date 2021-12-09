package com.NetCracker.Services.Doctor;


import com.NetCracker.Domain.DTO.RequestRatingDto;
import com.NetCracker.Entities.Doctor.Doctor;
import com.NetCracker.Entities.Doctor.DoctorRating;
import com.NetCracker.Entities.Doctor.UserStub;
import com.NetCracker.Repositories.AllUserRepository;
import com.NetCracker.Repositories.Doctor.DoctorRatingRepository;
import com.NetCracker.Repositories.Doctor.DoctorUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorRatingServiceImpl {

    private final DoctorRatingRepository doctorRatingRepository;
    private final DoctorUserRepository doctorRepository;
    private final AllUserRepository userRepository;

    public DoctorRating createRating(RequestRatingDto requestRatingDto){
        Optional<UserStub> optionalUser = userRepository.findById(requestRatingDto.getUserId());
        Optional<Doctor> optionalDoctor = doctorRepository.findById(requestRatingDto.getDoctorId());
//        Optional<User> optional1User = userRepository.findById(requestRatingDto.getDoctorId());

        if (optionalDoctor.isPresent() && optionalUser.isPresent()
                && requestRatingDto.getRating()>=1 && requestRatingDto.getRating()<=5){

            DoctorRating doctorRating= new DoctorRating();
            doctorRating.setDoctor(optionalDoctor.get());
            doctorRating.setUser(optionalUser.get());
            doctorRating.setRate(requestRatingDto.getRating());
            doctorRating.setFeedback(requestRatingDto.getFeedback());

            return doctorRatingRepository.save(doctorRating);
        }
        return null;
    }
}
