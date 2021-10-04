package com.NetCracker.Entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * This is a POJO class that represents schedule of a user (doctor or client).
 * Class consists of an appointment list and a reference to an appropriate user.
 */

@Entity
@Table(name = "schedule")
public class Schedule
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    @NotNull
    private Integer id;

    //This object is a TreeSet because we want to stored appointments in a chronological order
    //Also get, remove and add operations are all common in our type of application.
    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotNull
    private TreeSet<Appointment> appointments;

    @OneToOne(mappedBy = "User", cascade = CascadeType.ALL)
    @NotNull
    private User relatedTo;

    /**
     * This constructor is to be used by Hibernate only
     */
    @Deprecated
    public Schedule()
    {}

    public Schedule(TreeSet<Appointment> appointments, User relatedTo)
    {
        this.appointments = appointments;
        this.relatedTo = relatedTo;
        relatedTo.setSchedule(this);
    }

    /**
     * Creates a schedule that has no appointments yet.
     * @param relatedTo defines a user whose schedule is constructed
     */
    public Schedule(User relatedTo)
    {
        this.appointments = new TreeSet<>(Appointment.getChronologicalComparator());
        this.relatedTo = relatedTo;
        relatedTo.setSchedule(this);
    }

    /**
     * This method is to be used by Hibernate only
     * @param id is never updated in database
     */
    @Deprecated
    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setAppointments(TreeSet<Appointment> appointments)
    {
        this.appointments = appointments;
    }

    public void setRelatedTo(User relatedTo)
    {
        this.relatedTo = relatedTo;
    }

    public Integer getId()
    {
        return id;
    }

    public TreeSet<Appointment> getAppointments()
    {
        return appointments;
    }

    public User getRelatedTo()
    {
        return relatedTo;
    }

    /**
     * Helper method to easily add an appointment to this schedule.
     * Method takes care of a bidirectional association
     * @param appointment - an object that needs to be added to this schedule.
     */
    public void addAppointment(Appointment appointment)
    {
        //TODO - refactor this to search through a collection only once
        Appointment appointmentBeforeInserted = this.appointments.floor(appointment);
        if (appointmentBeforeInserted != null && appointmentBeforeInserted.getAppointmentEnd().isAfter(appointment.getAppointmentStart()))
        {
            throw new IllegalArgumentException("Cannot assign appointment to a schedule. There already is an appointment " + appointmentBeforeInserted.getName() + " at " + appointment.getAppointmentStart());
        }

        Appointment appointmentAfterInserted = this.appointments.ceiling(appointment);
        if (appointmentAfterInserted != null && appointmentAfterInserted.getAppointmentStart().isBefore(appointment.getAppointmentEnd()))
        {
            throw new IllegalArgumentException("Cannot assign appointment to a schedule. There already is an appointment " + appointmentAfterInserted.getName() + " at " + appointment.getAppointmentEnd());
        }

        this.appointments.add(appointment);
        appointment.setSchedule(this);
    }
}