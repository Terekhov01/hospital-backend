package com.NetCracker.services.doctor;

import com.NetCracker.domain.DTO.DoctorUnionUserDto;
import com.NetCracker.domain.DTO.UserDto;
import com.NetCracker.entities.doctor.Doctor;
import org.springframework.data.jpa.repository.Query;
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

    void save(Doctor doctor);

    void delete(Long id);

    List<Doctor> findAll();

    Doctor findById(Long id);

    Doctor findByRelatedUserId(Long id);
    DoctorUnionUserDto update(DoctorUnionUserDto user);

    List<DoctorShortInfo> findShortInfoBySpecializationName(String specializationName);

    List<DoctorUnionUserDto> findAllDoctorWithName();

    Long countAll();
}
