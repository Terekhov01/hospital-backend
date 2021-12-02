package com.NetCracker.Entities.Schedule;

import com.NetCracker.Entities.DoctorStub;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;
import com.google.gson.annotations.Expose;
import org.hibernate.annotations.SortComparator;

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
    @Expose
    private Long id;

    /**
     * Must contain TreeSet instance!
     */
    @NotNull
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "doctorSchedule")
    @SortComparator(ScheduleInterval.ScheduleIntervalDateAscendComparator.class)
    @Expose
    private SortedSet<ScheduleInterval> stateSet;

    @NotNull
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    @Expose(serialize = false, deserialize = false)
    private DoctorStub relatedDoctor;

    /**
     * For Hibernate only
     */
    @Deprecated
    public DoctorSchedule()
    {}

    public DoctorSchedule(DoctorStub relatedDoctor)
    {
        this.relatedDoctor = relatedDoctor;
        stateSet = new TreeSet<ScheduleInterval>(ScheduleInterval.dateAscendComparator);
    }

    public DoctorSchedule(DoctorStub relatedDoctor, TreeSet<ScheduleInterval> stateSet)
    {
        this(relatedDoctor);
        this.stateSet = stateSet;
    }

    public Long getId()
    {
        return id;
    }

    public SortedSet<ScheduleInterval> getStateSet() throws IllegalStateException
    {
        return stateSet;
    }

    public DoctorStub getRelatedDoctor()
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

    public void setStateSet(SortedSet<ScheduleInterval> stateSet)
    {
        this.stateSet = stateSet;
    }

    public void setRelatedDoctor(DoctorStub doctor)
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
