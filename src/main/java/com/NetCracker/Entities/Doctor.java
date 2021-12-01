package com.NetCracker.Entities;

import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Inheritance
@Table(name = "doctor")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Doctor extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "DOCTOR_ID", unique = true, nullable = false)
    private Long id;

    @OneToOne(mappedBy = "relatedDoctor")
    @JsonBackReference
    DoctorSchedule schedule;

    @Column(name = "SPECIALIZATION")
    @NotNull
    private String specialization;

    @Column(name = "ADDRESS")
    @NotNull
    private String address;

    @Column(name = "ROOM")
    @NotNull
    private String room;

    public DoctorSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(DoctorSchedule schedule) {
        this.schedule = schedule;
    }

    public Doctor() {
        super();
    }

    public Doctor(String lastName, String specialization, String address, String room) {
        super(lastName);
        this.address = address;
        this.specialization = specialization;
        this.room = room;
    }

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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor)) return false;
        Doctor doctor = (Doctor) o;
        return getId().equals(doctor.getId()) && Objects.equals(getSchedule(), doctor.getSchedule()) && getSpecialization().equals(doctor.getSpecialization()) && getAddress().equals(doctor.getAddress()) && getRoom().equals(doctor.getRoom());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSchedule(), getSpecialization(), getAddress(), getRoom());
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", schedule=" + schedule +
                ", specialization='" + specialization + '\'' +
                ", address='" + address + '\'' +
                ", room='" + room + '\'' +
                '}';
    }
}
