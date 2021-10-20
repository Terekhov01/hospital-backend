package com.NetCracker.Repositories;

import com.NetCracker.Entities.Doctor;
import com.NetCracker.Entities.Schedule.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long>
{
    DoctorSchedule findByRelatedDoctor(Doctor doctor);
}
