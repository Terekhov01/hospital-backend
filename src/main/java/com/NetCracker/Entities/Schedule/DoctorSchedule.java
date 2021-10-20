package com.NetCracker.Entities.Schedule;

import com.NetCracker.Entities.Doctor;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "doctor_schedule")
public class DoctorSchedule
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    private Long id;

    @NotNull
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "doctorSchedule")
    //@JoinColumn(name = "doctor_schedule_id", referencedColumnName = "id", nullable = false)
    private Set<ScheduleInterval> stateSet;

    @NotNull
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private Doctor relatedDoctor;

    /**
     * For Hibernate only
     */
    @Deprecated
    public DoctorSchedule()
    {}

    public DoctorSchedule(Doctor relatedDoctor)
    {
        this.relatedDoctor = relatedDoctor;
        stateSet = new TreeSet<>(ScheduleInterval.dateAscendComparator);
    }

    public DoctorSchedule(Doctor relatedDoctor, Set<ScheduleInterval> stateSet)
    {
        this(relatedDoctor);
        this.stateSet = stateSet;
    }

    public Long getId()
    {
        return id;
    }

    public Set<ScheduleInterval> getStateSet()
    {
        return stateSet;
    }

    public Doctor getRelatedDoctor()
    {
        return relatedDoctor;
    }

    /**
     * This method is to be used by Hibernate or testing only
     * @param id is never updated in database
     */
    @Deprecated
    public void setId(Long id)
    {
        this.id = id;
    }

    public void setStateSet(Set<ScheduleInterval> stateSet)
    {
        this.stateSet = stateSet;
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
        return id.equals(schedule.id) && stateSet.equals(schedule.stateSet);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, stateSet);
    }
}
