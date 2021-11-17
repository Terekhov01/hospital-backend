package com.NetCracker.Entities;


import javax.persistence.*;
import java.util.Arrays;
import java.util.Set;

@Entity
@Table(name = "MedCard")
public class MedCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER)
    private Patient patient;

    private String contraindications;
    private String hereditary;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Appointment> appointments;

    public MedCard() {
    }

    public MedCard(Patient patient, String contraindications, String hereditary, Set<Appointment> appointments) {
        this.patient = patient;
        this.contraindications = contraindications;
        this.hereditary = hereditary;
        this.appointments = appointments;
    }

    public void deleteAppointments(Set<Appointment> appointments){
        this.appointments.removeAll(appointments);
    }

    public void addAppointments(Set<Appointment> appointments){
        this.appointments.addAll(appointments);
    }

    public void deleteAppointments(Appointment... appointments){
        Arrays.asList(appointments).forEach(this.appointments::remove);
    }

//    public void deleteAppointment(Appointment appointment){
//        appointments.remove(appointment);
//    }

    public void addAppointments(Appointment... appointments){
        this.appointments.addAll(Arrays.asList(appointments));
    }

//    public void addAppointment(Appointment appointment){
//        appointments.add(appointment);
//    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getContraindications() {
        return contraindications;
    }

    public void setContraindications(String contraindications) {
        this.contraindications = contraindications;
    }

    public String getHereditary() {
        return hereditary;
    }

    public void setHereditary(String hereditary) {
        this.hereditary = hereditary;
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }
}
