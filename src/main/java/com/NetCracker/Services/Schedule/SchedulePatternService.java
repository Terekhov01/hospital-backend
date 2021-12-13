package com.NetCracker.Services.Schedule;

import com.NetCracker.Entities.Doctor.Doctor;
import com.NetCracker.Entities.Schedule.ScheduleElements.SchedulePatternInterval;
import com.NetCracker.Entities.Schedule.SchedulePattern;
import com.NetCracker.Repositories.Schedule.SchedulePatternIntervalRepository;
import com.NetCracker.Repositories.Schedule.SchedulePatternRepository;
import com.NetCracker.Services.Doctor.DoctorUserService;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.*;

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
    public SchedulePattern findPatternByName(String patternName)
    {
        return schedulePatternRepository.findByName(patternName).orElse(null);
    }

    static class SchedulePatternRepresentation
    {
        String patternName;
        Integer daysLength;
        ScheduleDayPattern[] scheduleDailyPatterns;
    }

    static class ScheduleDayPattern
    {
        Integer dayNumber;

        @SerializedName(value = "timesRounded")
        List<LocalTime> intervalStart;
    }

    public SchedulePattern fromJson(String patternStr, String doctorName)
    {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(LocalTime.class, new JsonDeserializer<LocalTime>() {
                @Override
                public LocalTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
                {
                    String timeStr = json.getAsString();
                    String[] hourMinuteStrings = timeStr.split(":");
                    int hour = Integer.parseInt(hourMinuteStrings[0]);
                    int minute = Integer.parseInt(hourMinuteStrings[1]);
                    return LocalTime.of(hour, minute);
                }
            }
        );

        Gson gson = gsonBuilder.create();
        SchedulePatternRepresentation schedulePatternRepresentation = gson.fromJson(patternStr, SchedulePatternRepresentation.class);

        //TODO - change doctor
        Doctor relatedDoctor = doctorUserService.findById(1L);

        if (relatedDoctor == null)
        {
            return null;
        }

        NavigableSet<SchedulePatternInterval> schedulePatternIntervals = new TreeSet<SchedulePatternInterval>(SchedulePatternInterval.dateAscendComparator);

        for (var scheduleDayPattern : schedulePatternRepresentation.scheduleDailyPatterns)
        {
            for (var interval : scheduleDayPattern.intervalStart)
            {
                schedulePatternIntervals.add(new SchedulePatternInterval(scheduleDayPattern.dayNumber, interval));
            }
        }

        return new SchedulePattern(relatedDoctor,
                                    schedulePatternRepresentation.patternName,
                                    schedulePatternRepresentation.daysLength,
                                    schedulePatternIntervals);
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