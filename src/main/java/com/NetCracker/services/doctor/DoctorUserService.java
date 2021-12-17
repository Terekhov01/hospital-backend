package com.NetCracker.services.doctor;

import com.NetCracker.domain.DTO.UserDto;
import com.NetCracker.entities.doctor.Doctor;

import java.util.List;

public interface DoctorUserService {

    Doctor create(UserDto user);

    void delete(Long id);

    List<Doctor> findAll();

    Doctor findById(Long id);

    Doctor findByRelatedUserId(Long id);

    Doctor update(Doctor user);
}