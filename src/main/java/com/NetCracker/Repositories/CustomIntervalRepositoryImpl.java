package com.NetCracker.Repositories;

import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class CustomIntervalRepositoryImpl implements CustomIntervalRepository
{
    @PersistenceContext
    private EntityManager em;

    @Override
    public void addInterval(DoctorSchedule schedule, Iterable<ScheduleInterval> scheduledStates)
    {
        scheduledStates.forEach(state -> addInterval(schedule, state));
    }

    @Override
    @Transactional
    public void addInterval(DoctorSchedule schedule, ScheduleInterval state)
    {
        Query insertStateQuery = em.createNativeQuery("INSERT INTO schedule_interval (doctor_schedule_id, interval_start_time, is_assigned) SELECT ?, ?, ?");
        insertStateQuery.setParameter(1, schedule.getId());
        insertStateQuery.setParameter(2, state.getIntervalStartTime());
        insertStateQuery.setParameter(3, state.isAssigned());
        insertStateQuery.executeUpdate();
    }
}
