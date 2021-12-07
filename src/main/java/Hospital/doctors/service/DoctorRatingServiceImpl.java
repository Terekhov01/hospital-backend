package Hospital.doctors.service;


import Hospital.doctors.domain.dto.RequestRatingDto;
import Hospital.doctors.domain.entity.Doctor;
import Hospital.doctors.domain.entity.DoctorRating;
import Hospital.doctors.domain.entity.User;
import Hospital.doctors.repository.AllUserRepository;
import Hospital.doctors.repository.DoctorRatingRepository;
import Hospital.doctors.repository.DoctorUserRepository;
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
        Optional<User> optionalUser = userRepository.findById(requestRatingDto.getUserId());
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
