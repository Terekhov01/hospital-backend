package com.NetCracker.Services;

import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Service
public class ScheduleViewService
{
    @Autowired
    ScheduleService scheduleService;

    static class DoctorData
    {
        @Expose
        Long id;

        @Expose
        String specialization;

        @Expose
        String doctorName;

        @Expose
        Set<ScheduleInterval> intervalSet;

        public DoctorData(Long id, String specialization, /*TODO - refactor name when doctor entity will release*/String doctorName, SortedSet<ScheduleInterval> intervalSet)
        {
            this.id = id;
            this.specialization = specialization;
            this.doctorName = doctorName;
            this.intervalSet = intervalSet;
        }

        public Set<ScheduleInterval> getIntervalSet()
        {
            return intervalSet;
        }
    }

    public String getScheduleTableJson(Long[] doctorIds, LocalDateTime startDateTime, LocalDateTime endDateTime/*, ZoneOffset zoneOffset*/, Boolean getFreeTimeOnly)
    {
        Set<DoctorSchedule> schedules = scheduleService.getDoctorSchedule(Arrays.stream(doctorIds).toList());

        ScheduleInterval intervalStart = new ScheduleInterval(startDateTime);
        ScheduleInterval intervalEnd = new ScheduleInterval(endDateTime);

        //Required data - each doctor is matched with set of scheduleIntervals - time, when (s)he is working (not busy)
        Set<DoctorData> doctorDataSet = schedules.stream().map(schedule ->
                        new DoctorData(schedule.getRelatedDoctor().getId(), /*TODO - get doctor specialization*/"ABC",
                                schedule.getRelatedDoctor().getName(), schedule.getStateSet().subSet(intervalStart, intervalEnd)))
                .collect(Collectors.toSet());
                /*collect(
                Collectors.toSet(
                        schedule -> new DoctorData(schedule.getRelatedDoctor().getId(), "ABC",
                                    schedule.getRelatedDoctor().getName(), schedule.getStateSet().subSet(intervalStart, intervalEnd))
                )
        );*/

        if (getFreeTimeOnly)
        {
            //Remove all assigned intervals
            for (var doctorData : doctorDataSet)
            {
                doctorData.getIntervalSet().removeIf(ScheduleInterval::isAssigned);
            }

            //Remove all entries that have empty value
            doctorDataSet.removeIf(doctorData -> doctorData.getIntervalSet().size() == 0);
            /*doctorDataSet = doctorDataSet.entrySet().stream().filter(pair ->
                            pair.getValue().getIntervalSet().size() != 0)
                    .map(pair -> entry(pair.getKey(), pair.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));*/
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

        Gson gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().create();

        return gson.toJson(doctorDataSet);
    }
}
