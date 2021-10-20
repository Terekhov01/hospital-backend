package com.NetCracker.Services;

import com.NetCracker.Entities.Doctor;
import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.NetCracker.Entities.Schedule.SchedulePattern;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;
import com.NetCracker.Repositories.DoctorRepository;
import com.NetCracker.Repositories.DoctorScheduleRepository;
import com.NetCracker.Repositories.ScheduleIntervalRepository;
import com.NetCracker.Repositories.SchedulePatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

/**
 * This is a class that represents schedule of all doctors.
 * It is used to manipulate schedules.
 * This class is a singleton.
 */

@Service
public class ScheduleService
{
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    private ScheduleIntervalRepository scheduleIntervalRepository;

    @Autowired
    private SchedulePatternRepository schedulePatternRepository;

    public ScheduleService()
    {
    }

    public DoctorSchedule getDoctorSchedule(Doctor doctor)
    {
        return doctorScheduleRepository.findByRelatedDoctor(doctor);
    }

    public List<Doctor> getAllDoctors()
    {
        return doctorRepository.findAll();
    }

    public List<DoctorSchedule> getAllDoctorSchedules()
    {
        return doctorScheduleRepository.findAll();
    }

    @Transactional
    public void addSchedule(DoctorSchedule schedule)
    {
        /*if (doctorRepository.findById(doctor.getId()).isEmpty())
        {
            System.err.println("No doctor to add schedule " + schedule.toString() + " to.");
            return;
        }

        schedule.setRelatedDoctor(doctor);*/
        doctorScheduleRepository.save(schedule);
    }

    @Transactional
    public void removeSchedule(Iterable<DoctorSchedule> schedules)
    {
        schedules.forEach(this::removeSchedule);
    }

    @Transactional()
    public void removeSchedule(DoctorSchedule schedule)
    {
        doctorScheduleRepository.delete(schedule);
    }

    @Transactional
    public void addInterval(ScheduleInterval interval)
    {
        scheduleIntervalRepository.save(interval);
    }

    @Transactional
    public void prolongScheduleByPattern(Doctor doctor, SchedulePattern pattern, LocalDate dayToApplyPatternFrom)
    {
        DoctorSchedule schedule = getDoctorSchedule(doctor);

        if (schedule == null)
        {
            System.err.println("Doctor " + doctor.toString() + " has no schedule to prolong.");
            return;
        }

        Set<ScheduleInterval> scheduleIntervalSet = schedule.getStateSet();

        pattern.getStateSet().forEach(patternInterval ->
                scheduleIntervalSet.add(new ScheduleInterval(schedule, dayToApplyPatternFrom, patternInterval))
                );

        schedule.setStateSet(scheduleIntervalSet);
        scheduleIntervalRepository.saveAll(scheduleIntervalSet);
        //scheduleIntervalRepository.addInterval(schedule, scheduleIntervalSet);
    }

    /**
     * This function allows you to get information about any time in doctor's schedule.
     * It presents you a status as an output.
     * @param doctor doctor, whose schedule to look up.
     * @param time time in schedule you are interested in.
     * @return null, if there is no schedule specified for the doctor at the time or if doctor won't work then
     * or status of a doctor.
     */
    @Transactional
    public ScheduleInterval getStatus(Doctor doctor, LocalDateTime time)
    {
        ScheduleInterval state = scheduleIntervalRepository.findByDoctorAndTime(doctor.getId(), time);
        return state;
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}