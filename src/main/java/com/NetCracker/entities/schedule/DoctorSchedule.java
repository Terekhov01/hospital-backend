package com.NetCracker.entities.schedule;

import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.schedule.scheduleElements.ScheduleInterval;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @NotNull
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REMOVE, mappedBy = "doctorSchedule")
    @SortComparator(ScheduleInterval.ScheduleIntervalDateAscendComparator.class)
    @Expose
    @JsonManagedReference
    private SortedSet<ScheduleInterval> stateSet;

    @NotNull
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    @Expose(serialize = false, deserialize = false)
    @JsonManagedReference
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
        stateSet = new TreeSet<ScheduleInterval>(ScheduleInterval.dateAscendComparator);
    }

    public DoctorSchedule(Doctor relatedDoctor, TreeSet<ScheduleInterval> stateSet)
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

    public void setStateSet(SortedSet<ScheduleInterval> stateSet)
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
