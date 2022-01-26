package com.NetCracker.entities.schedule.scheduleElements;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ScheduleIntervalId implements Serializable
{
    private Long doctorSchedule;

    private LocalDateTime startTime;

    public ScheduleIntervalId()
    {
    }

    public ScheduleIntervalId(Long doctorSchedule, LocalDateTime startTime)
    {
        this.doctorSchedule = doctorSchedule;
        this.startTime = startTime;
    }

    public Long getDoctorSchedule()
    {
        return doctorSchedule;
    }

    public LocalDateTime getStartTime()
    {
        return startTime;
    }

    public void setDoctorSchedule(Long doctorSchedule)
    {
        this.doctorSchedule = doctorSchedule;
    }

    public void setStartTime(LocalDateTime startTime)
    {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ScheduleIntervalId)) return false;
        ScheduleIntervalId that = (ScheduleIntervalId) o;
        return Objects.equals(doctorSchedule, that.doctorSchedule) && Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(doctorSchedule, startTime);
    }
}
