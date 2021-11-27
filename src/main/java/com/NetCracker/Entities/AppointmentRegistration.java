package com.NetCracker.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "appointment_registration")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AppointmentRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "APPOINTMENT_REGISTRATION_ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "SERVICE")
    @NotNull
    private String service;

    @Column(name = "START_DATE_TIME")
    @NotNull
    private LocalDateTime start;

    @Column(name = "END_DATE_TIME")
    private LocalDateTime end;

    @Column(name = "ADDRESS")
    @NotNull
    private String address;

    @Column(name = "ROOM")
    @NotNull
    private String room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PATIENT", referencedColumnName = "PATIENT_ID")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCTOR", referencedColumnName = "DOCTOR_ID")
    private Doctor doctor;

    public AppointmentRegistration() {
    }

//    public AppointmentRegistration(LocalDateTime start, LocalDateTime end,
//                                   String address, String room, Patient patient,
//                                   Doctor doctor) {
//        this.start = start;
//        this.end = end;
//        this.address = address;
//        this.room = room;
//        this.patient = patient;
//        this.doctor = doctor;
//    }


    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public AppointmentRegistration(String service, LocalDateTime start,
                                   LocalDateTime end, String address, String room,
                                   Patient patient, Doctor doctor) {
        this.service = service;
        this.start = start;
        this.end = end;
        this.address = address;
        this.room = room;
        this.patient = patient;
        this.doctor = doctor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof AppointmentRegistration)) return false;
//        AppointmentRegistration that = (AppointmentRegistration) o;
//        return getId() == that.getId() && getStart().equals(that.getStart()) &&
//                Objects.equals(getEnd(), that.getEnd()) &&
//                getAddress().equals(that.getAddress()) &&
//                getRoom().equals(that.getRoom()) &&
//                getPatient().equals(that.getPatient()) &&
//                getDoctor().equals(that.getDoctor());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getId(), getStart(), getEnd(), getAddress(), getRoom(), getPatient(), getDoctor());
//    }
//
//    @Override
//    public String toString() {
//        return "AppointmentRegistration{" +
//                "id=" + id +
//                ", start=" + start +
//                ", end=" + end +
//                ", address='" + address + '\'' +
//                ", room='" + room + '\'' +
//                ", patient=" + patient +
//                ", doctor=" + doctor +
//                '}';
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppointmentRegistration)) return false;
        AppointmentRegistration that = (AppointmentRegistration) o;
        return getId() == that.getId() && getService().equals(that.getService()) && getStart().equals(that.getStart()) && Objects.equals(getEnd(), that.getEnd()) && getAddress().equals(that.getAddress()) && getRoom().equals(that.getRoom()) && Objects.equals(getPatient(), that.getPatient()) && Objects.equals(getDoctor(), that.getDoctor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getService(), getStart(), getEnd(), getAddress(), getRoom(), getPatient(), getDoctor());
    }

    @Override
    public String toString() {
        return "AppointmentRegistration{" +
                "id=" + id +
                ", service='" + service + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", address='" + address + '\'' +
                ", room='" + room + '\'' +
                ", patient=" + patient +
                ", doctor=" + doctor +
                '}';
    }
}
