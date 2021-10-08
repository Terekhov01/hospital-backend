package com.NetCracker.Entities;

import javax.persistence.*;

@Entity
@Table(name = "doctor")
public class Doctor
{
    //TODO - design and implement doctor entity

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    //TODO - Implement constructor for this class. Remove @Deprecated if needed
    @Deprecated
    public Doctor()
    {
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }
}
