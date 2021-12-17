package com.NetCracker.services.schedule;

import com.NetCracker.entities.schedule.scheduleElements.SchedulePatternInterval;
import com.NetCracker.entities.schedule.SchedulePattern;
import com.NetCracker.repositories.schedule.SchedulePatternIntervalRepository;
import com.NetCracker.repositories.schedule.SchedulePatternRepository;
import com.NetCracker.services.doctor.DoctorUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Methods that help to interact with schedule pattern entity are implemented here
 */
@Service
public class SchedulePatternService
{
    @Autowired
    SchedulePatternRepository schedulePatternRepository;

    @Autowired
    SchedulePatternIntervalRepository schedulePatternIntervalRepository;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    DoctorUserService doctorUserService;

    /**
     * Saves pattern and all of it's intervals to database. Does not check if pattern has a unique name.
     * @param pattern value to store in database
     */
    @Transactional
    public void save(SchedulePattern pattern)
    {
        schedulePatternRepository.save(pattern);
        addInterval(pattern.getStateSet());
    }

    @Transactional
    public void remove(SchedulePattern pattern)
    {
        schedulePatternRepository.delete(pattern);
    }

    @Transactional
    public void remove(Iterable<SchedulePattern> pattern)
    {
        schedulePatternRepository.deleteAll(pattern);
    }

    @Transactional
    public void addInterval(Iterable<SchedulePatternInterval> intervals)
    {
        intervals.forEach(this::addInterval);
    }

    @Transactional
    public void addInterval(SchedulePatternInterval interval)
    {
        schedulePatternIntervalRepository.save(interval);
        interval.getSchedulePattern().getStateSet().add(interval);
    }

    @Transactional
    public void removeInterval(Iterable<SchedulePatternInterval> intervals)
    {
        schedulePatternIntervalRepository.deleteAll(intervals);
    }

    @Transactional
    public void removeInterval(SchedulePatternInterval interval)
    {
        schedulePatternIntervalRepository.delete(interval);
    }

    @Transactional
    public SchedulePattern getSchedulePattern(Long id)
    {
        return schedulePatternRepository.findById(id).orElse(null);
    }

    @Transactional
    public SchedulePattern findPatternByNameAndRelatedDoctor(String patternName, Long doctorId)
    {
        return schedulePatternRepository.findByNameAndRelatedDoctor(patternName, doctorId).orElse(null);
    }

    public List<SchedulePattern> getAllPatterns()
    {
        return schedulePatternRepository.findAll();
    }

    public List<SchedulePattern> getPatternsByDoctor(Long doctorId)
    {
        return schedulePatternRepository.findByRelatedDoctorId(doctorId);
    }
}
