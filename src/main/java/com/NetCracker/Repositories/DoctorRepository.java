package com.NetCracker.Repositories;

import com.NetCracker.Entities.AppointmentRegistration;
import com.NetCracker.Entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    @Query("select a from Doctor a order by a.id asc")
    public List<Doctor> findAllByOrderByIdAsc();

    @Query("select a from Doctor a where a.id = :id")
    Optional<Doctor> findById(@Param("id") Integer id);

    @Query("select a from Doctor a where a.lastName = :doc")
    Optional<Doctor> findDoctorByLastName(@Param("doc") String doc);

}
