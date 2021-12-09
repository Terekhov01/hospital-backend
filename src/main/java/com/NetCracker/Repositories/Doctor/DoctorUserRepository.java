package com.NetCracker.Repositories.Doctor;

import com.NetCracker.Entities.Doctor.Doctor;
import com.NetCracker.Entities.Doctor.Specialist;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DoctorUserRepository extends Repository<Doctor, Integer> {

    void delete(Doctor user);

    List<Doctor> findAll();

    Optional<Doctor> findById(Long id);

    Doctor save(Doctor user);

    interface DoctorShortInformation
    {
        Long getId();
        String getFirstName();
        String getLastName();
        Set<Specialist> getSpecialist();
        //TODO - add specialization
    }

    //findByIdInOrderBySpecializationAscNameAsc
    Collection<DoctorShortInformation> findByIdInOrderByLastNameAscFirstNameAsc(Collection<Long> doctorIds);
}
