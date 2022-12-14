package com.NetCracker.repositories.schedule;

import com.NetCracker.entities.schedule.scheduleElements.ScheduleInterval;
import com.NetCracker.entities.schedule.scheduleElements.ScheduleIntervalId;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ScheduleIntervalRepository extends JpaRepository<ScheduleInterval, ScheduleIntervalId>
{
    @Query(value = "SELECT * FROM " +
            "((SELECT * FROM doctor_schedule doc_sch WHERE doctor_id = :id) ds " +
            "JOIN schedule_interval si ON ds.id = si.doctor_schedule_id) sch_int " +
            "WHERE interval_start_time = :time", nativeQuery = true)
    ScheduleInterval findByDoctorAndTime(@Param("id") Long id, @Param("time") LocalDateTime time) throws DataAccessException;

    @Query("select si from ScheduleInterval si where si.doctorSchedule.id = :id and si.startTime = :time")
    Optional<ScheduleInterval> findScheduleIntervalByDoctorScheduleIdAndIntervalStartTime(@Param("id") Long id, @Param("time") LocalDateTime time);

}
