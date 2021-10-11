package com.NetCracker.Entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
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
    private Long id;

    private @NotNull LocalDate scheduleStartDate;

    @NotNull
    @CollectionTable(name = "schedule_interval", joinColumns = @JoinColumn(name = "doctor_schedule_id"))
    @ElementCollection(fetch = FetchType.LAZY, targetClass = ScheduleState.class)
    private List<ScheduleState> intervalStatusList;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private Doctor relatedDoctor;

    /**
     * This constructor is for hibernate use only
     */
    @Deprecated
    public DoctorSchedule()
    {}

    public DoctorSchedule(LocalDate scheduleStartDate)
    {
        this.scheduleStartDate = scheduleStartDate;
        intervalStatusList = new ArrayList<>();
    }

    public DoctorSchedule(LocalDate scheduleStartDate, List<ScheduleState> intervalStatusList)
    {
        this(scheduleStartDate);
        this.intervalStatusList = intervalStatusList;
    }

    public Long getId()
    {
        return id;
    }

    public @NotNull LocalDate getScheduleStartDate()
    {
        return scheduleStartDate;
    }

    public List<ScheduleState> getIntervalStatusList()
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
    public void setScheduleStartDate(@NotNull LocalDate time)
    {
        this.scheduleStartDate = time;
    }

    public void setRelatedDoctor(Doctor doctor)
    {
        relatedDoctor = doctor;
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
