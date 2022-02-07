package com.NetCracker.repositories.patient;

import com.NetCracker.entities.patient.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    @Query("select f from File f order by f.id asc")
    List<File> findAllByOrderByIdAsc();

    @Query("select f from File f where f.id = :id")
    Optional<File> findById(@Param("id") Long id);

    @Query("select f from File f where f.patient.id = :id")
    List<File> findByPatientId(@Param("id") Long id);// mb return list
}
