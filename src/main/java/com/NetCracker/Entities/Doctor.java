package com.NetCracker.Entities;

import com.NetCracker.Entities.Schedule.DoctorSchedule;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "doctor")
public class Doctor
{
    //TODO - design and implement doctor entity

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "relatedDoctor")
    DoctorSchedule schedule;

    String name;

    //TODO - Implement constructor for this class. Remove @Deprecated if needed
    @Deprecated
    public Doctor()
    {
        schedule = null;
    }

    public Doctor(String name)
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Doctor)) return false;
        Doctor doctor = (Doctor) o;
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
