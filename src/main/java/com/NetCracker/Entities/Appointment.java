package com.NetCracker.Entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "APPOINTMENT_ID", unique = true, nullable = false)
    private int id;

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

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SERVICE")
    private String service;

    @Column(name = "RECIPE")
    private String recipe;

    @Lob
    @Column(name = "TREATMENT_PLAN")
    private String treatPlan;

    @Lob
    @Column(name = "REHABILITATION_PLAN")
    private String rehabPlan;

    @Column(name = "DOCTORS_STATEMENT")
    private String docStatement;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public String getTreatPlan() {
        return treatPlan;
    }

    public void setTreatPlan(String treatPlan) {
        this.treatPlan = treatPlan;
    }

    public String getRehabPlan() {
        return rehabPlan;
    }

    public void setRehabPlan(String rehabPlan) {
        this.rehabPlan = rehabPlan;
    }

    public String getDocStatement() {
        return docStatement;
    }

    public void setDocStatement(String docStatement) {
        this.docStatement = docStatement;
    }
}
