package com.NetCracker.Services;

import com.NetCracker.Entities.Doctor;
import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleIntervalId;
import com.NetCracker.Entities.Schedule.SchedulePattern;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;
import com.NetCracker.Repositories.DoctorRepository;
import com.NetCracker.Repositories.DoctorScheduleRepository;
import com.NetCracker.Repositories.ScheduleIntervalRepository;
import com.NetCracker.Repositories.SchedulePatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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

    public Set<DoctorSchedule> getDoctorSchedule(Collection<Long> doctorIds) throws DataAccessException
    {
        return doctorScheduleRepository.findByRelatedDoctor(doctorIds);
    }

    public DoctorSchedule getDoctorSchedule(Doctor doctor) throws DataAccessException
    {
        return doctorScheduleRepository.findByRelatedDoctor(doctor.getId());
    }

    /*public Set<DoctorSchedule> getDoctorSchedule(Collection<String> doctorNames)
    {
        Set<DoctorSchedule> schedules = new HashSet<>();
        doctorNames.forEach(name -> schedules.add(getDoctorSchedule(name)));

        return schedules;
    }

    public DoctorSchedule getDoctorSchedule(String doctorName)
    {
        return doctorScheduleRepository.findByName()
    }*/

    public List<Doctor> getAllDoctors() throws DataAccessException
    {
        return doctorRepository.findAll();
    }

    public List<DoctorSchedule> getAllDoctorSchedules() throws DataAccessException
    {
        return doctorScheduleRepository.findAll();
    }

    @Transactional
    public void add(Iterable<DoctorSchedule> schedules) throws DataAccessException
    {
        schedules.forEach(this::add);
    }

    @Transactional
    public void add(DoctorSchedule schedule) throws DataAccessException
    {
        doctorScheduleRepository.save(schedule);
    }

    @Transactional
    public void remove(Iterable<DoctorSchedule> schedules) throws DataAccessException
    {
        schedules.forEach(this::remove);
    }

    @Transactional()
    public void remove(DoctorSchedule schedule) throws DataAccessException
    {
        doctorScheduleRepository.delete(schedule);
    }

    @Transactional
    public void addInterval(ScheduleInterval interval) throws DataAccessException
    {
        scheduleIntervalRepository.save(interval);
    }

    @Transactional
    public void prolongScheduleByPattern(Doctor doctor, SchedulePattern pattern, LocalDate dayToApplyPatternFrom) throws DataAccessException
    {
        DoctorSchedule schedule = getDoctorSchedule(doctor);

        if (schedule == null)
        {
            System.err.println("Doctor " + doctor.toString() + " has no schedule to prolong.");
            return;
        }

        TreeSet<ScheduleInterval> scheduleIntervalSet = schedule.getStateSet();

        pattern.getStateSet().forEach(patternInterval ->
                scheduleIntervalSet.add(new ScheduleInterval(schedule, dayToApplyPatternFrom, patternInterval))
                );

        schedule.setStateSet(scheduleIntervalSet);
        scheduleIntervalRepository.saveAll(scheduleIntervalSet);
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
    public ScheduleInterval getDoctorStatus(Doctor doctor, LocalDateTime time) throws DataAccessException
    {
        return scheduleIntervalRepository.getById(new ScheduleIntervalId(doctor.getId(), time));
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}