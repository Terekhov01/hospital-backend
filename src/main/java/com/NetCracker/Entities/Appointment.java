package com.NetCracker.Entities;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Comparator;

@Entity
public class Appointment
{
    //TODO - probably refactor me!
    @NotNull
    private LocalDateTime appointmentStart;
    @NotNull
    private LocalDateTime appointmentEnd;

    String name;

    Schedule schedule;

    public LocalDateTime getAppointmentStart()
    {
        return appointmentStart;
    }

    public LocalDateTime getAppointmentEnd()
    {
        return appointmentEnd;
    }

    public String getName()
    {
        return name;
    }

    public void setAppointmentStart(LocalDateTime appointmentStart)
    {
        this.appointmentStart = appointmentStart;
    }

    public void setAppointmentEnd(LocalDateTime appointmentEnd)
    {
        this.appointmentEnd = appointmentEnd;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setSchedule(Schedule schedule)
    {
        this.schedule = schedule;
    }

    /**
     * Compares two appointments based on their start date and time
     * @return a comparator which compare() method returns negative integer if less and positive if greater
     */
    public static Comparator<Appointment> getChronologicalComparator()
    {
        return new Comparator<Appointment>()
        {
            @Override
            public int compare(@NotNull Appointment appointment1, @NotNull Appointment appointment2)
            {
                return appointment1.getAppointmentStart().compareTo(appointment2.getAppointmentStart());
            }
        };
    }
}
