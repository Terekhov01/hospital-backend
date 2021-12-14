package com.NetCracker.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "patients")
public class Patient
{
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JsonIgnore
    private User user;

    @Column
    private String passport;

    @Column
    private String polys;

    public Patient(String passport, String polys)
    {
        this.passport = passport;
        this.polys = polys;
    }

    public Patient() {
    }
}
