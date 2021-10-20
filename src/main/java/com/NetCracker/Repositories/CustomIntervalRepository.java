package com.NetCracker.Repositories;

import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;

public interface CustomIntervalRepository
{
    void addInterval(DoctorSchedule schedule, Iterable<ScheduleInterval> scheduledStates);
    void addInterval(DoctorSchedule schedule, ScheduleInterval state);
}
