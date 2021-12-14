package com.NetCracker.Services.Doctor;

import com.NetCracker.Domain.DTO.UserDto;
import com.NetCracker.Entities.Doctor.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorUserService {

    Doctor create(UserDto user);

    void delete(Long id);

    List<Doctor> findAll();

    Doctor findById(Long id);

    Doctor findByRelatedUserId(Long id);

    Doctor update(Doctor user);
}