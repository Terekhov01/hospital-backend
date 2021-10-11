package com.NetCracker.Repositories;

import com.NetCracker.Entities.DoctorSchedule;
import com.NetCracker.Entities.ScheduleState;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class CustomDoctorScheduleRepositoryImpl implements CustomDoctorScheduleRepository
{
    @PersistenceContext
    private EntityManager em;

    @Override
    public void pushBackToSchedule(DoctorSchedule schedule, Iterable<ScheduleState> scheduledStates)
    {
        scheduledStates.forEach(state -> pushBackToSchedule(schedule, state));
    }

    @Override
    @Transactional
    public void pushBackToSchedule(DoctorSchedule schedule, ScheduleState state)
    {
        Query insertStateQuery = em.createNativeQuery("INSERT INTO schedule_interval (doctor_schedule_id, state) SELECT ?, ?");
        insertStateQuery.setParameter(1, schedule.getId());
        insertStateQuery.setParameter(2, state.getState());
        insertStateQuery.executeUpdate();
    }
}
