package com.NetCracker.Repositories;

import com.NetCracker.Entities.Doctor.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialistRepository extends JpaRepository<Specialist,Integer> {

    List<Specialist> findBySpecializationIn(List<String> specialization);
}
