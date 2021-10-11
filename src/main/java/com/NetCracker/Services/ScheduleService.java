package com.NetCracker.Services;

import com.NetCracker.Entities.Doctor;
import com.NetCracker.Entities.DoctorSchedule;
import com.NetCracker.Entities.SchedulePattern;
import com.NetCracker.Entities.ScheduleState;
import com.NetCracker.Repositories.DoctorRepository;
import com.NetCracker.Repositories.DoctorScheduleRepository;
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
    public void addDoctorSchedule(Doctor doctor, DoctorSchedule schedule)
    {
        if (doctorRepository.findById(doctor.getId()).isEmpty())
        {
            System.err.println("No doctor to add schedule " + schedule.toString() + " to.");
            return;
        }

        schedule.setRelatedDoctor(doctor);
        doctorScheduleRepository.save(schedule);
    }

    @Transactional
    public void prolongScheduleByPattern(Doctor doctor, SchedulePattern pattern)
    {
        DoctorSchedule schedule = getDoctorSchedule(doctor);

        if (schedule == null)
        {
            System.err.println("Doctor " + doctor.toString() + " has no schedule to prolong.");
            return;
        }

        doctorScheduleRepository.pushBackToSchedule(schedule, pattern.getStatusList());

        /*pattern.getStatusList().forEach(status ->
                schedule.getIntervalStatusList().add(status));*/
    }

    /**
     * This function allows you to get information about any time in doctor's schedule.
     * It calculates offsets and presents you a status.
     * @param doctor doctor, whose schedule to look up.
     * @param time time in schedule you are interested in.
     * @return null, if there is no schedule for specified doctor if schedule does not contain specified date
     * or status of a doctor.
     */
    @Transactional
    public ScheduleState getStatus(Doctor doctor, LocalDateTime time)
    {
        DoctorSchedule schedule = getDoctorSchedule(doctor);

        if (schedule == null)
        {
            return null;
        }

        Integer offset = getOffset(schedule, time);
        if (offset == null)
        {
            return null;
        }

        return schedule.getIntervalStatusList().get(offset);
    }

    /**
     * This function calculates offset in List<scheduleStatus> of given schedule
     * where the state of specified date and time is stored
     * @param schedule to calculate offset for
     * @param dateTime that you are interested in
     * @return null - if schedule does not contain information about dateTime,
     * or else index of associated scheduleStatus in List of schedule statuses
     */
    @Transactional
    protected static Integer getOffset(DoctorSchedule schedule, LocalDateTime dateTime)
    {
        int scheduleSize = schedule.getIntervalStatusList().size();
        Integer hoursOffset = getOffset(schedule, dateTime.toLocalDate(), scheduleSize);
        Integer minutesOffset = getOffset(schedule, dateTime.toLocalTime(), scheduleSize);

        if (hoursOffset == null || minutesOffset == null)
        {
            return null;
        }

        int totalOffset = hoursOffset + minutesOffset;

        if (totalOffset >= schedule.getIntervalStatusList().size())
        {
            return null;
        }

        return totalOffset;
    }

    /**
     * This function calculates offset in List<scheduleState> of given schedule
     * where the state of specified date at 00(h):00(m):00(s) is stored
     * @param schedule to calculate offset for
     * @param date     that you are interested in
     * @return null - if schedule does not contain information about dateTime,
     * or else index of state in list that corresponds to date at beginning of the day
     */
    protected static Integer getOffset(DoctorSchedule schedule, LocalDate date, int scheduleSize)
    {
        if (schedule.getScheduleStartDate().isAfter(date))
        {
            return null;
        }

        long daysDifference = Period.between(schedule.getScheduleStartDate(), date).getDays();

        // Possible overflow is checked below
        long offsetInList = daysDifference * 48;

        if (daysDifference > (Integer.MAX_VALUE / 2 - 1) || offsetInList >= scheduleSize)
        {
            // Parameter date points to the moment of time that is after the last scheduled interval
            return null;
        }

        return (int) offsetInList;
    }

    /**
     * This function calculates offset in List<scheduleState> of given schedule
     * where the state of specified time is stored
     * @param schedule to calculate offset for
     * @param time     that you are interested in
     * @return null - if schedule does not contain information about specified time (at neither date),
     * or an integer (0-47) that represents an offset in array for specified time.
     */
    @Transactional
    protected static Integer getOffset(DoctorSchedule schedule, LocalTime time, int scheduleSize)
    {
        long minutesDifference = Duration.between(schedule.getScheduleStartDate().atStartOfDay().toLocalTime(), time).toMinutes();
        long offsetInList = minutesDifference / 30;
        if (offsetInList >= scheduleSize)
        {
            return null;
        }
        return (int) offsetInList;
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}