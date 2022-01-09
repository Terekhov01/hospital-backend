package com.NetCracker.services.doctor;

import com.NetCracker.domain.DTO.DoctorUnionUserDto;
import com.NetCracker.domain.DTO.UserDto;
import com.NetCracker.entities.doctor.Doctor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorUserService {


    Doctor create(UserDto user);

    void delete(Long id);


//    @Query("select u.firstName,u.lastName,d.dateOfEmployment,d.education,d.room,d.specialist,d.ratings" +
//            " from User u INNER JOIN Doctor d where d.id =u.id")
    List<Doctor> findAll();

    Doctor findById(Long id);

    //DoctorInfoController
//    DoctorUnionUserDto findedById(Long id);

    Doctor findByRelatedUserId(Long id);
    DoctorUnionUserDto update(DoctorUnionUserDto user);

    DoctorUnionUserDto findDoctorWithName(Long id);
    List<DoctorUnionUserDto> findAllDoctorWithName();


}