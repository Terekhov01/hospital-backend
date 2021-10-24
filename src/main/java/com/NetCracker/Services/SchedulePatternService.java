package com.NetCracker.Services;

import com.NetCracker.Entities.Schedule.ScheduleElements.SchedulePatternInterval;
import com.NetCracker.Entities.Schedule.SchedulePattern;
import com.NetCracker.Repositories.SchedulePatternIntervalRepository;
import com.NetCracker.Repositories.SchedulePatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

public class SchedulePatternService
{
    @Autowired
    SchedulePatternRepository schedulePatternRepository;

    @Autowired
    SchedulePatternIntervalRepository schedulePatternIntervalRepository;

    /**
     * Saves pattern and all of it's intervals to database
     * @param pattern value to store in database
     */
    @Transactional
    public void add(SchedulePattern pattern)
    {
        schedulePatternRepository.save(pattern);
        schedulePatternIntervalRepository.saveAll(pattern.getStateSet());
    }

    /**
     * Saves patterns and all of their intervals to database
     * @param patterns values to store in database
     */
    @Transactional
    public void add(Iterable<SchedulePattern> patterns)
    {
        schedulePatternRepository.saveAll(patterns);
        patterns.forEach(pattern -> schedulePatternIntervalRepository.saveAll(pattern.getStateSet()));
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
    public void  addInterval(Iterable<SchedulePatternInterval> intervals)
    {
        schedulePatternIntervalRepository.saveAll(intervals);
    }

    @Transactional
    public void addInterval(SchedulePatternInterval interval)
    {
        schedulePatternIntervalRepository.save(interval);
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
}
