package com.NetCracker.Repositories.Schedule;

import com.NetCracker.Entities.Schedule.SchedulePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchedulePatternRepository extends JpaRepository<SchedulePattern, Long>
{
    @Query("FROM SchedulePattern WHERE relatedDoctor.id = :id")//as pattern JOIN Doctor as doctor ON pattern.doctor_id = doctor.id WHERE doctor.id = :id")
    List<SchedulePattern> findByRelatedDoctorId(@Param("id") Long doctor_id);

    Optional<SchedulePattern> findByName(String name);
}
