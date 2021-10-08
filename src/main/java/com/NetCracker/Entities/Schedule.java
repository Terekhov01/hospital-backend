package com.NetCracker.Entities;

import com.NetCracker.Repositories.DoctorScheduleRepository;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a class that represents schedule of all doctors.
 * This class is a singleton.
 */

public class Schedule
{
    private static Schedule instance;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable/*(name = "doctor_schedule_mapping", joinColumns =
            {
                @JoinColumn(name = "schedule_id", referencedColumnName = "id")
            },
            inverseJoinColumns =
            {
                @JoinColumn(name = "doctor_schedule_id", referencedColumnName = "id")
            }
            )*/
    //@MapKeyJoinColumn(name = "doctor_id", referencedColumnName = "id")
    Map<Doctor, DoctorSchedule> doctorsSchedule;

    public static Schedule getInstance()
    {
        if (instance == null)
        {
            instance = new Schedule();
        }
        return instance;
    }

    public Schedule()
    {
        doctorsSchedule = new HashMap<>();
    }

    public DoctorSchedule getDoctorSchedule(Doctor doctor)
    {
        return doctorsSchedule.get(doctor);
    }

    public Collection<DoctorSchedule> getAllDoctorSchedules()
    {
        return doctorsSchedule.values();
    }

    public void addDoctorSchedule(Doctor doctor, DoctorSchedule schedule)
    {
        doctorsSchedule.put(doctor, schedule);
    }
}