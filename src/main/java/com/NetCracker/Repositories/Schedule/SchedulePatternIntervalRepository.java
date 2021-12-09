package com.NetCracker.Repositories.Schedule;

import com.NetCracker.Entities.Schedule.ScheduleElements.SchedulePatternInterval;
import com.NetCracker.Entities.Schedule.ScheduleElements.SchedulePatternIntervalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulePatternIntervalRepository extends JpaRepository<SchedulePatternInterval, SchedulePatternIntervalId>
{
}
