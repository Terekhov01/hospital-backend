package com.NetCracker.Entities;

import javax.persistence.*;

@Entity
@Table(name = "doctor")
public class Doctor
{
    //TODO - design and implement doctor entity

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /*@OneToOne(mappedBy = "doctor_schedule")
    private DoctorSchedule schedule;*/

    //TODO - Implement constructor for this class. Remove @Deprecated if needed
    @Deprecated
    public Doctor()
    {
    }

    public Long getId()
    {
        return id;
    }

    /*public DoctorSchedule getSchedule()
    {
        return schedule;
    }*/

    public void setId(Long id)
    {
        this.id = id;
    }

    /*public void setSchedule(DoctorSchedule schedule)
    {
        this.schedule = schedule;
    }*/
}
