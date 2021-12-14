package com.NetCracker.repositories.patient;

import com.NetCracker.entities.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("select a from Patient a order by a.id asc")
    public List<Patient> findAllByOrderByIdAsc();

    @Query("select a from Patient a where a.id = :id")
    Optional<Patient> findById(@Param("id") Long id);

    @Query("select a from Patient a where a.user.lastName = :pat")
    Optional<Patient> findPatientByLastName(@Param("pat") String pat);

}
