package com.NetCracker.Entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "doctor_schedule")
public class DoctorSchedule
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    @NotNull
    private Long id;

    // Always points to start of the day.
    // Used LocalDateTime instead of LocalDate to avoid many type cast operations in business logic.
    @NotNull
    LocalDateTime scheduleStartDate;

    @NotNull
    @Embedded
    @ElementCollection
    List<ScheduleStatus> intervalStatusList;

    /**
     * This constructor is for hibernate use only
     */
    @Deprecated
    public DoctorSchedule()
    {}

    public DoctorSchedule(LocalDate scheduleStartDate)
    {
        this.scheduleStartDate = scheduleStartDate.atStartOfDay();
        intervalStatusList = new ArrayList<>();
    }

    public DoctorSchedule(LocalDate scheduleStartDate, List<ScheduleStatus> intervalStatusList)
    {
        this(scheduleStartDate);
        this.intervalStatusList = intervalStatusList;
    }

    public Long getId()
    {
        return id;
    }

    public LocalDateTime getScheduleStartDate()
    {
        return scheduleStartDate;
    }

    public List<ScheduleStatus> getIntervalStatusList()
    {
        return intervalStatusList;
    }

    /**
     * This method is to be used by Hibernate only
     * @param id is never updated in database
     */
    @Deprecated
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * This method is to be used by Hibernate only.
     * You should not refresh schedule using this method. It's not meant to do so.
     * You might want to use ScheduleMaintenanceService.prolongScheduleByWorkingPattern()
     * @param time is the time that schedule is assumed to start
     */
    @Deprecated
    public void setScheduleStartDate(LocalDateTime time)
    {
        this.scheduleStartDate = time;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof DoctorSchedule)) return false;
        DoctorSchedule schedule = (DoctorSchedule) o;
        return id.equals(schedule.id) && scheduleStartDate.equals(schedule.scheduleStartDate) && intervalStatusList.equals(schedule.intervalStatusList);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, scheduleStartDate, intervalStatusList);
    }
}
