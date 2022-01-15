package com.NetCracker.repositories.doctor;

import com.NetCracker.entities.doctor.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialistRepository extends JpaRepository<Specialist,Integer> {

    List<Specialist> findBySpecializationIn(List<String> specialization);

    //Getting specialization names only
    @Query(nativeQuery = true, value = "SELECT specialization FROM specialist ORDER BY specialization")
    List<String> findSpecializationByOrderBySpecialization();
}
