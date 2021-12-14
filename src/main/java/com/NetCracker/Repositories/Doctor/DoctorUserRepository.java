package com.NetCracker.Repositories.Doctor;

import com.NetCracker.Entities.Doctor.Doctor;
import com.NetCracker.Entities.Doctor.Specialist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DoctorUserRepository extends Repository<Doctor, Integer> {

    void delete(Doctor user);

    List<Doctor> findAll();

    Optional<Doctor> findById(Long id);

    Optional<Doctor> findByUser_id(Long id);

    Doctor save(Doctor user);

    interface DoctorShortInformation
    {
        Long getId();
        String getFirstName();
        String getLastName();
        Set<Specialist> getSpecialist();
    }

    //findByIdInOrderBySpecializationAscNameAsc
    @Query("FROM Doctor WHERE id IN :id ORDER BY user.lastName ASC, user.firstName ASC")
    Collection<DoctorShortInformation> findByIdInOrderByLastNameAscFirstNameAsc(@Param("id") Collection<Long> doctorIds);
}
