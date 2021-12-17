package com.NetCracker.repositories.schedule;

import com.NetCracker.entities.schedule.scheduleElements.SchedulePatternInterval;
import com.NetCracker.entities.schedule.scheduleElements.SchedulePatternIntervalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulePatternIntervalRepository extends JpaRepository<SchedulePatternInterval, SchedulePatternIntervalId>
{
}
