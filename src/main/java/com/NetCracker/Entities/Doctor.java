package com.NetCracker.Entities;

import javax.persistence.*;

@Entity
@Table(name = "doctor")
public class Doctor extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "DOCTOR_ID", unique = true, nullable = false)
    private int id;

}
