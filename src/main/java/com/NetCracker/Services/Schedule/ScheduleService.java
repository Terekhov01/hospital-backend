package com.NetCracker.Services.Schedule;

import com.NetCracker.Entities.Doctor.Doctor;
import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleIntervalId;
import com.NetCracker.Entities.Schedule.SchedulePattern;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;
import com.NetCracker.Repositories.Doctor.DoctorUserRepository;
import com.NetCracker.Repositories.Schedule.DoctorScheduleRepository;
import com.NetCracker.Repositories.Schedule.ScheduleIntervalRepository;
import com.NetCracker.Repositories.Schedule.SchedulePatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

/**
 * Methods that help to interact with doctor schedule entity are implemented here
 */
@Service
public class ScheduleService
{
    @Autowired
    private DoctorUserRepository doctorRepository;

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
        //return doctor.getSchedule();
        return doctorScheduleRepository.findByRelatedDoctor(doctor.getId());
    }

    public List<DoctorSchedule> getAllDoctorSchedules() throws DataAccessException
    {
        return doctorScheduleRepository.findAll();
    }

    @Transactional
    public void save(Iterable<DoctorSchedule> schedules) throws DataAccessException
    {
        schedules.forEach(this::save);
    }

    @Transactional
    public void save(DoctorSchedule schedule) throws DataAccessException
    {
        doctorScheduleRepository.save(schedule);
        schedule.getRelatedDoctor().setSchedule(schedule);
    }

    @Transactional
    public void delete(Iterable<DoctorSchedule> schedules) throws DataAccessException
    {
        schedules.forEach(this::delete);
    }

    @Transactional
    public void delete(DoctorSchedule schedule) throws DataAccessException
    {
        doctorScheduleRepository.delete(schedule);
        schedule.getRelatedDoctor().setSchedule(null);
    }

    @Transactional
    public void addInterval(ScheduleInterval interval) throws DataAccessException
    {
        scheduleIntervalRepository.save(interval);
        interval.getDoctorSchedule().getStateSet().add(interval);
    }

    @Transactional
    public void applyPatternToSchedule(Doctor doctor, SchedulePattern pattern, LocalDate dayToApplyPatternFrom) throws DataAccessException
    {
        DoctorSchedule schedule = getDoctorSchedule(doctor);

        if (schedule == null)
        {
            throw new DataRetrievalFailureException("No schedule for doctor with id " + doctor.getId() + " exists!");
        }

        SortedSet<ScheduleInterval> scheduleIntervalSet = schedule.getStateSet();

        final LocalDate dayToApplyPatternTo = dayToApplyPatternFrom.plusDays(pattern.getDaysLength());

        scheduleIntervalSet.stream().filter(interval -> (interval.getIntervalStartTime().toLocalDate().isAfter(dayToApplyPatternFrom)
                || interval.getIntervalStartTime().toLocalDate().isEqual(dayToApplyPatternFrom))
                && interval.getIntervalStartTime().toLocalDate().isBefore(dayToApplyPatternTo)).forEach(interval -> scheduleIntervalRepository.delete(interval));

        scheduleIntervalSet.removeIf(interval ->
                (interval.getIntervalStartTime().toLocalDate().isAfter(dayToApplyPatternFrom)
                        || interval.getIntervalStartTime().toLocalDate().isEqual(dayToApplyPatternFrom))
                        && interval.getIntervalStartTime().toLocalDate().isBefore(dayToApplyPatternTo));

        pattern.getStateSet().forEach(patternInterval ->
                scheduleIntervalSet.add(new ScheduleInterval(schedule, dayToApplyPatternFrom, patternInterval))
                );

        scheduleIntervalRepository.saveAll(scheduleIntervalSet);
        schedule.setStateSet(scheduleIntervalSet);
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
    public ScheduleInterval getDoctorState(Doctor doctor, LocalDateTime time) throws DataAccessException
    {
        if (doctor.getSchedule() == null)
        {
            return null;
        }

        return scheduleIntervalRepository.findById(new ScheduleIntervalId(doctor.getSchedule().getId(), time)).orElse(null);
    }

    //TODO - change when doctor service is ready to use
    Doctor getDoctorById(Long doctorId)
    {
        return doctorRepository.findById(doctorId).orElse(null);
    }

    /**
     * This function returns shortened information about doctors. It is used to help users set filters.
     * @param doctorIds ids of doctors whose information to return
     * @throws DataAccessException if retrieving information failed
     */
    @Transactional
    public Collection<DoctorUserRepository.DoctorShortInformation> getDoctorShortInformation(List<Long> doctorIds) throws DataAccessException
    {
        return doctorRepository.findByIdInOrderByLastNameAscFirstNameAsc(doctorIds);
    }
}