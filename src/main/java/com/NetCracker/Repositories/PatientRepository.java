package com.NetCracker.Repositories;

import com.NetCracker.Entities.Doctor;
import com.NetCracker.Entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    @Query("select a from Patient a order by a.id asc")
    public List<Patient> findAllByOrderByIdAsc();

    @Query("select a from Patient a where a.id = :id")
    Optional<Patient> findById(@Param("id") Integer id);

    @Query("select a from Patient a where a.lastName = :pat")
    Optional<Patient> findPatientByLastName(@Param("pat") String pat);

}