package com.NetCracker.Repositories;

import com.NetCracker.Entities.DoctorSchedule;
import com.NetCracker.Entities.ScheduleState;

public interface CustomDoctorScheduleRepository
{
    void pushBackToSchedule(DoctorSchedule schedule, Iterable<ScheduleState> scheduledStates);
    void pushBackToSchedule(DoctorSchedule schedule, ScheduleState state);
}
