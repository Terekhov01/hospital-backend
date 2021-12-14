package com.NetCracker.services.doctor;


import com.NetCracker.domain.DTO.RequestRatingDto;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.doctor.DoctorRating;
import com.NetCracker.entities.doctor.UserStub;
import com.NetCracker.repositories.AllUserRepository;
import com.NetCracker.repositories.doctor.DoctorRatingRepository;
import com.NetCracker.repositories.doctor.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorRatingServiceImpl {

    private final DoctorRatingRepository doctorRatingRepository;
    private final DoctorRepository doctorRepository;
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
