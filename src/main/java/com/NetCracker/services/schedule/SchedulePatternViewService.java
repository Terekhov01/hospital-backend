package com.NetCracker.services.schedule;

import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.schedule.scheduleElements.SchedulePatternInterval;
import com.NetCracker.entities.schedule.SchedulePattern;
import com.NetCracker.services.doctor.DoctorUserService;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * Service contains methods that correspond to schedule pattern.
 * They are used to transferring data to/from (angular) client.
 */
@Service
public class SchedulePatternViewService
{
    @Autowired
    ScheduleService scheduleService;

    @Autowired
    SchedulePatternService schedulePatternService;

    @Autowired
    DoctorUserService doctorUserService;

    public String getSchedulePatternList(Long doctorId)
    {
        List<SchedulePattern> schedulePatternList = schedulePatternService.getPatternsByDoctor(doctorId);

        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(SchedulePattern.class, new JsonSerializer<SchedulePattern>()
        {
            @Override
            public JsonElement serialize(SchedulePattern pattern, Type type, JsonSerializationContext jsonDeserializationContext) throws JsonParseException
            {
                JsonObject patternObject = new JsonObject();
                patternObject.add("id", new JsonPrimitive(pattern.getId()));
                patternObject.add("name", new JsonPrimitive(pattern.getName()));
                patternObject.add("daysLength", new JsonPrimitive(pattern.getDaysLength()));

                return patternObject;
            }
        });

        Gson gson = gsonBuilder.create();

        return gson.toJson(schedulePatternList);
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
        List<LocalTime> intervalsStartTime;

        public ScheduleDayPattern()
        {
            intervalsStartTime = new ArrayList<>();
        }
    }

    public SchedulePattern fromJson(String patternStr, Long doctorId) throws InvalidParameterException, ChangeSetPersister.NotFoundException
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
        SchedulePatternViewService.SchedulePatternRepresentation schedulePatternRepresentation = gson.fromJson(patternStr, SchedulePatternViewService.SchedulePatternRepresentation.class);

        if (schedulePatternRepresentation.patternName.equals(""))
        {
            throw new InvalidParameterException("Name of pattern cannot be empty!");
        }

        Doctor relatedDoctor = doctorUserService.findById(doctorId);

        if (relatedDoctor == null)
        {
            throw new ChangeSetPersister.NotFoundException();
        }

        NavigableSet<SchedulePatternInterval> schedulePatternIntervals = new TreeSet<SchedulePatternInterval>(SchedulePatternInterval.dateAscendComparator);

        for (var scheduleDayPattern : schedulePatternRepresentation.scheduleDailyPatterns)
        {
            for (var interval : scheduleDayPattern.intervalsStartTime)
            {
                schedulePatternIntervals.add(new SchedulePatternInterval(scheduleDayPattern.dayNumber, interval));
            }
        }

        return new SchedulePattern(relatedDoctor,
                schedulePatternRepresentation.patternName,
                schedulePatternRepresentation.daysLength,
                schedulePatternIntervals);
    }

    public String toJson(String patternName, Long doctorId) throws AccessDeniedException
    {
        SchedulePattern requestedPattern = schedulePatternService.findPatternByNameAndRelatedDoctor(patternName, doctorId);
        if (requestedPattern == null)
        {
            throw new AccessDeniedException("Pattern does not exist or doctor has no access to it");
        }


        SchedulePatternRepresentation schedulePatternRepresentation = new SchedulePatternRepresentation();
        schedulePatternRepresentation.patternName = requestedPattern.getName();
        schedulePatternRepresentation.daysLength = requestedPattern.getDaysLength();

        schedulePatternRepresentation.scheduleDailyPatterns = new ScheduleDayPattern[requestedPattern.getDaysLength()];

        for (int dayCounter = 0; dayCounter < requestedPattern.getDaysLength(); dayCounter++)
        {
            schedulePatternRepresentation.scheduleDailyPatterns[dayCounter] = new ScheduleDayPattern();
            schedulePatternRepresentation.scheduleDailyPatterns[dayCounter].dayNumber = dayCounter;
        }

        for (SchedulePatternInterval interval : requestedPattern.getStateSet())
        {
            schedulePatternRepresentation.scheduleDailyPatterns[interval.getDayNumber()].intervalsStartTime
                                                                                .add(interval.getIntervalStartTime());
        }

        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(LocalTime.class, new JsonSerializer<LocalTime>()
        {
            @Override
            public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context)
            {
                JsonObject timeObject = new JsonObject();
                timeObject.add("hour", new JsonPrimitive(src.getHour()));
                timeObject.add("minute", new JsonPrimitive(src.getMinute()));
                return timeObject;
            }
        });

        Gson gson = gsonBuilder.create();

        return gson.toJson(schedulePatternRepresentation);
    }
}
