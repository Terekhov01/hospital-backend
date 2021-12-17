package com.NetCracker.entities.schedule.scheduleElements;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ScheduleIntervalId implements Serializable
{
    private Long doctorSchedule;

    private LocalDateTime intervalStartTime;

    public ScheduleIntervalId()
    {
    }

    public ScheduleIntervalId(Long doctorSchedule, LocalDateTime intervalStartTime)
    {
        this.doctorSchedule = doctorSchedule;
        this.intervalStartTime = intervalStartTime;
    }

    public Long getDoctorSchedule()
    {
        return doctorSchedule;
    }

    public LocalDateTime getIntervalStartTime()
    {
        return intervalStartTime;
    }

    public void setDoctorSchedule(Long doctorSchedule)
    {
        this.doctorSchedule = doctorSchedule;
    }

    public void setIntervalStartTime(LocalDateTime intervalStartTime)
    {
        this.intervalStartTime = intervalStartTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ScheduleIntervalId)) return false;
        ScheduleIntervalId that = (ScheduleIntervalId) o;
        return Objects.equals(doctorSchedule, that.doctorSchedule) && Objects.equals(intervalStartTime, that.intervalStartTime);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(doctorSchedule, intervalStartTime);
    }
}
