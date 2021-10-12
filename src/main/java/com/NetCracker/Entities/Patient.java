package com.NetCracker.Entities;

import javax.persistence.*;

@Entity
@Table(name = "patient")
public class Patient extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PATIENT_ID", unique = true, nullable = false)
    private int id;

}
