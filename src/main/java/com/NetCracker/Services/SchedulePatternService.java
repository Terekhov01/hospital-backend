package com.NetCracker.Services;

import com.NetCracker.Entities.Schedule.ScheduleElements.SchedulePatternInterval;
import com.NetCracker.Entities.Schedule.SchedulePattern;
import com.NetCracker.Repositories.SchedulePatternIntervalRepository;
import com.NetCracker.Repositories.SchedulePatternRepository;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

@Service
public class SchedulePatternService {
    @Autowired
    SchedulePatternRepository schedulePatternRepository;

    @Autowired
    SchedulePatternIntervalRepository schedulePatternIntervalRepository;

    /**
     * Saves pattern and all of it's intervals to database
     *
     * @param pattern value to store in database
     */
    @Transactional
    public void save(SchedulePattern pattern) {
        schedulePatternRepository.save(pattern);
        addInterval(pattern.getStateSet());
    }

    /**
     * Saves patterns and all of their intervals to database
     *
     * @param patterns values to store in database
     */
    @Transactional
    public void save(Iterable<SchedulePattern> patterns) {
        schedulePatternRepository.saveAll(patterns);
        patterns.forEach(pattern -> addInterval(pattern.getStateSet()));
    }

    @Transactional
    public void remove(SchedulePattern pattern) {
        schedulePatternRepository.delete(pattern);
    }

    @Transactional
    public void remove(Iterable<SchedulePattern> pattern) {
        schedulePatternRepository.deleteAll(pattern);
    }

    @Transactional
    public void addInterval(Iterable<SchedulePatternInterval> intervals) {
        intervals.forEach(this::addInterval);
    }

    @Transactional
    public void addInterval(SchedulePatternInterval interval) {
        schedulePatternIntervalRepository.save(interval);
        interval.getSchedulePattern().getStateSet().add(interval);
    }

    @Transactional
    public void removeInterval(Iterable<SchedulePatternInterval> intervals) {
        schedulePatternIntervalRepository.deleteAll(intervals);
    }

    @Transactional
    public void removeInterval(SchedulePatternInterval interval) {
        schedulePatternIntervalRepository.delete(interval);
    }

    @Transactional
    public SchedulePattern getSchedulePattern(Long id) {
        return schedulePatternRepository.findById(id).orElse(null);
    }

    static class ScheduleDayPattern {
        Integer dayNumber;

        @SerializedName(value = "timesRounded")
        List<LocalTime> intervalStart;
    }

    public SchedulePattern fromJson(String patternStr) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(LocalTime.class, new JsonDeserializer<LocalTime>() {
                    @Override
                    public LocalTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                        String timeStr = json.getAsString();
                        String[] hourMinuteStrings = timeStr.split(":");
                        int hour = Integer.parseInt(hourMinuteStrings[0]);
                        int minute = Integer.parseInt(hourMinuteStrings[1]);
                        return LocalTime.of(hour, minute);
                    }
                }
        );

        Gson gson = gsonBuilder.create();
        ScheduleDayPattern[] scheduleDayPatterns = gson.fromJson(patternStr, ScheduleDayPattern[].class);

        SchedulePattern schedulePattern = new SchedulePattern("Goose");

        for (var scheduleDayPattern : scheduleDayPatterns) {
            for (var interval : scheduleDayPattern.intervalStart) {
                schedulePattern.getStateSet().add(new SchedulePatternInterval(scheduleDayPattern.dayNumber, LocalDateTime.of(2021, Month.NOVEMBER, 22, interval.getHour(), interval.getMinute()), schedulePattern));

            }
        }

        return schedulePattern;
    }
}
