package com.NetCracker.services.doctor;

import com.NetCracker.domain.DTO.UserDto;
import com.NetCracker.entities.doctor.Doctor;
import lombok.AllArgsConstructor;

import java.util.List;

public interface DoctorUserService {
    @AllArgsConstructor
    class DoctorShortInfo
    {
        Long id;
        String firstName;
        String lastName;
        String middleName;
    }

    Doctor create(UserDto user);

    void delete(Long id);

    List<Doctor> findAll();

    Doctor findById(Long id);

    Doctor findByRelatedUserId(Long id);

    List<DoctorShortInfo> findShortInfoBySpecializationName(String specializationName);

    Doctor update(Doctor user);
}