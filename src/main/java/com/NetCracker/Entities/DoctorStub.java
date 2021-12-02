package com.NetCracker.Entities;

import com.NetCracker.Entities.Schedule.DoctorSchedule;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "doctor")
public class DoctorStub
{
    //TODO - design and implement doctor entity

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "relatedDoctor")
    DoctorSchedule schedule;

    @NotNull
    String name;

    @NotNull
    String specialization;

    //TODO - Implement constructor for this class. Remove @Deprecated if needed
    @Deprecated
    public DoctorStub()
    {
        schedule = null;
    }

    public DoctorStub(String name)
    {
        schedule = null;
        this.name = name;
    }

    public Long getId()
    {
        return id;
    }

    public DoctorSchedule getSchedule()
    {
        return schedule;
    }

    public String getName()
    {
        return name;
    }

    public String getSpecialization()
    {
        return specialization;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setSchedule(DoctorSchedule schedule)
    {
        this.schedule = schedule;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setSpecialization(String specialization)
    {
        this.specialization = specialization;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof DoctorStub)) return false;
        DoctorStub doctor = (DoctorStub) o;
        return id.equals(doctor.id) && Objects.equals(schedule.getId(), doctor.schedule.getId());
    }

    @Override
    public int hashCode()
    {
        if (schedule == null)
        {
            return Objects.hash(id);
        }
        return Objects.hash(id, schedule.getId());
    }
}
