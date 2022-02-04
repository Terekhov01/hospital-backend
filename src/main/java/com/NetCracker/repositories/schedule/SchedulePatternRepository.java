package com.NetCracker.repositories.schedule;

import com.NetCracker.entities.schedule.SchedulePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchedulePatternRepository extends JpaRepository<SchedulePattern, Long>
{
    void deleteByName(String name);

    @Query("FROM SchedulePattern WHERE relatedDoctor.id = :id")//as pattern JOIN Doctor as doctor ON pattern.doctor_id = doctor.id WHERE doctor.id = :id")
    List<SchedulePattern> findByRelatedDoctorId(@Param("id") Long doctor_id);

    @Query("FROM SchedulePattern WHERE relatedDoctor.id = :id AND name = :name")
    Optional<SchedulePattern> findByNameAndRelatedDoctor(@Param("name") String name, @Param("id") Long doctor_id);
}
