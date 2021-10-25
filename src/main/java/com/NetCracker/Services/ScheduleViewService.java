package com.NetCracker.Services;

import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;
import com.google.gson.*;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Service
public class ScheduleViewService
{
    @Autowired
    ScheduleService scheduleService;

    public String getScheduleTableJson(Long[] doctorIds, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean getFreeTimeOnly)
    {
        Set<DoctorSchedule> schedules = scheduleService.getDoctorSchedule(Arrays.stream(doctorIds).toList());

        ScheduleInterval intervalStart = new ScheduleInterval(startDateTime);
        ScheduleInterval intervalEnd = new ScheduleInterval(endDateTime);

        class MappedData
        {
            String specialization;
            String doctorName;
            SortedSet<ScheduleInterval> intervalSet;

            public MappedData(String specialization, String doctorName, SortedSet<ScheduleInterval> intervalSet)
            {
                this.specialization = specialization;
                this.doctorName = doctorName;
                this.intervalSet = intervalSet;
            }

            public SortedSet<ScheduleInterval> getIntervalSet()
            {
                return intervalSet;
            }
        }

        //Required data - each doctor is matched with set of scheduleIntervals - time, when (s)he is working (not busy)
        Map<Long, MappedData> scheduleRangedIntervals = schedules.stream().collect(
                Collectors.toMap(
                        schedule -> schedule.getRelatedDoctor().getId(),
                        schedule -> new MappedData(/*TODO - get doctor specialization*/"ABC",
                                    schedule.getRelatedDoctor().getName(), schedule.getStateSet().subSet(intervalStart, intervalEnd))
                )
        );

        if (getFreeTimeOnly)
        {
            //Remove all assigned intervals
            for (var intervalSet : scheduleRangedIntervals.values())
            {
                intervalSet.getIntervalSet().removeIf(ScheduleInterval::isAssigned);
            }

            //Remove all entries that have empty value
            scheduleRangedIntervals = scheduleRangedIntervals.entrySet().stream().filter(pair ->
                            pair.getValue().getIntervalSet().size() != 0)
                    .map(pair -> entry(pair.getKey(), pair.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>()
        {
            @Override
            public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonDeserializationContext) throws JsonParseException
            {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_INSTANT;
                return new JsonPrimitive(dateTimeFormatter.format(localDateTime));
            }
        });

        Gson gson = gsonBuilder.create();

        return gson.toJson(scheduleRangedIntervals);
    }
}
