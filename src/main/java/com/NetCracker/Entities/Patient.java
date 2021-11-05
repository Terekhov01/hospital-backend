package com.NetCracker.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Inheritance
@Table(name = "patient")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Patient extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PATIENT_ID", unique = true, nullable = false)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return super.getLastName();
    }

    public void setLastName(String lastName) {
        super.setLastName(lastName);
    }

    public Patient() {
        super();
    }

    public Patient(String lastName) {
        super(lastName);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                "lastName=" + this.getLastName() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient patient = (Patient) o;
        return getId() == patient.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), super.getLastName());
    }
}
