package Hospital.doctors.service;

import Hospital.doctors.domain.dto.UserDto;
import Hospital.doctors.domain.entity.Doctor;

import java.util.List;

public interface DoctorUserService {

    Doctor create(UserDto user);

    void delete(Integer id);

    List<Doctor> findAll();

    Doctor findById(int id);

    Doctor update(Doctor user);
}