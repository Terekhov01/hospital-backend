package com.NetCracker.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Inheritance
@Table(name = "patient")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Patient extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PATIENT_ID", unique = true, nullable = false)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "PATIENT")
    @JsonManagedReference
    private List<File> files;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
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
        return getId() == patient.getId() && Objects.equals(getFiles(), patient.getFiles());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), super.getLastName());
    }
}
