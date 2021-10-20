package com.NetCracker.Repositories;

import com.NetCracker.Entities.Schedule.ScheduleElements.SchedulePatternIntervalId;
import com.NetCracker.Entities.Schedule.SchedulePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulePatternRepository extends JpaRepository<SchedulePattern, SchedulePatternIntervalId>
{
}
