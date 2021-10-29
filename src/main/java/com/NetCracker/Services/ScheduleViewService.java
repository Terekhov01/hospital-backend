package com.NetCracker.Services;

import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleViewService
{
    @Autowired
    ScheduleService scheduleService;

    static class DoctorScheduleTableDataDaily
    {
        @Expose
        LocalDate date;

        @Expose
        LocalTime startOfDay;

        @Expose
        LocalTime endOfDay;

        public DoctorScheduleTableDataDaily(LocalDate date, LocalTime startOfDay, LocalTime endOfDay)
        {
            this.date = date;
            this.startOfDay = startOfDay;
            this.endOfDay = endOfDay;
        }

        public LocalDate getDate()
        {
            return date;
        }

        public LocalTime getStartOfDay()
        {
            return startOfDay;
        }

        public LocalTime getEndOfDay()
        {
            return endOfDay;
        }

        public static final Comparator<DoctorScheduleTableDataDaily> dateAscendComparator = new Comparator<DoctorScheduleTableDataDaily>() {
            @Override
            public int compare(DoctorScheduleTableDataDaily o1, DoctorScheduleTableDataDaily o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        };
    }

    static class DoctorScheduleTableData
    {
        //TODO - refactor name and specialization when doctor entity will release
        @Expose
        Long id;

        @Expose
        String specialization;

        @Expose
        String doctorName;

        @Expose
        SortedSet<DoctorScheduleTableDataDaily> dailyInformation;

        public DoctorScheduleTableData(DoctorSchedule schedule)
        {
            this.id = schedule.getRelatedDoctor().getId();
            this.specialization = "Goose";
            //this.specialization = schedule.getRelatedDoctor().getSpecialization().toString();
            this.doctorName = schedule.getRelatedDoctor().getName();
            this.dailyInformation = new TreeSet<DoctorScheduleTableDataDaily>(DoctorScheduleTableDataDaily.dateAscendComparator);
            var setIterator = schedule.getStateSet().iterator();

            LocalDate curDate = null;
            LocalTime startOfDay = null;
            LocalTime endOfDay = null;
            while (setIterator.hasNext())
            {
                var interval = setIterator.next();
                LocalDate intervalDate = interval.getIntervalStartTime().toLocalDate();
                LocalTime intervalTime = interval.getIntervalStartTime().toLocalTime();
                if (intervalDate.equals(curDate))
                {
                    endOfDay = intervalTime;
                }
                else
                {
                    if (curDate != null)
                    {
                        dailyInformation.add(new DoctorScheduleTableDataDaily(curDate, startOfDay, endOfDay));
                    }

                    curDate = intervalDate;
                    startOfDay = intervalTime;
                    endOfDay = intervalTime.plusMinutes(30);
                }
            }
        }

        /*public DoctorScheduleTableData(Long id, String specialization, String doctorName, SortedSet<ScheduleInterval> intervalSet)
        {
            this.id = id;
            this.specialization = specialization;
            this.doctorName = doctorName;
            this.dailyInformation = intervalSet;
        }

        public Set<ScheduleInterval> getIntervalSet()
        {
            return dailyInformation;
        }*/
    }

    static class DoctorScheduleAssignmentCalendarData
    {
        //TODO - refactor name and specialization when doctor entity will release
        @Expose
        Long id;

        @Expose
        String specialization;

        @Expose
        String doctorName;

        @Expose
        SortedSet<ScheduleInterval> dailyInformation;

        public DoctorScheduleAssignmentCalendarData(Long id, String specialization, String doctorName, SortedSet<ScheduleInterval> intervalSet)
        {
            this.id = id;
            this.specialization = specialization;
            this.doctorName = doctorName;
            this.dailyInformation = intervalSet;
        }

        public Set<ScheduleInterval> getIntervalSet()
        {
            return dailyInformation;
        }
    }

//    public String getScheduleTableJson(Long[] doctorIds, LocalDateTime startDateTime, LocalDateTime endDateTime/*, ZoneOffset zoneOffset*/, Boolean getFreeTimeOnly)
//    {
//        Set<DoctorSchedule> schedules = scheduleService.getDoctorSchedule(Arrays.stream(doctorIds).toList());
//
//        ScheduleInterval intervalStart = new ScheduleInterval(startDateTime);
//        ScheduleInterval intervalEnd = new ScheduleInterval(endDateTime);
//
//        //Required data - each doctor is matched with set of scheduleIntervals - time, when (s)he is working (not busy)
//        Set<DoctorScheduleTableData> doctorScheduleTableDataSet = schedules.stream().map(DoctorScheduleTableData::new)
//                .collect(Collectors.toSet());
//
//
//        if (getFreeTimeOnly)
//        {
//            //Remove all assigned intervals
//            for (var doctorData : doctorScheduleTableDataSet)
//            {
//                doctorData.getIntervalSet().removeIf(ScheduleInterval::isAssigned);
//            }
//
//            //Remove all entries that have empty value
//            doctorScheduleTableDataSet.removeIf(doctorScheduleTableData -> doctorScheduleTableData.getIntervalSet().size() == 0);
//        }
//
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>()
//        {
//            @Override
//            public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonDeserializationContext) throws JsonParseException
//            {
//                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
//                return new JsonPrimitive(dateTimeFormatter.format(localDateTime));
//            }
//        });
//
//        Gson gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
//
//        return gson.toJson(doctorScheduleTableDataSet);
//    }

    public String getScheduleAssignmentCalendarJson(Long[] doctorIds, LocalDateTime startDateTime, LocalDateTime endDateTime/*, ZoneOffset zoneOffset*/, Boolean getFreeTimeOnly)
    {
        Set<DoctorSchedule> schedules = scheduleService.getDoctorSchedule(Arrays.stream(doctorIds).toList());

        ScheduleInterval intervalStart = new ScheduleInterval(startDateTime);
        ScheduleInterval intervalEnd = new ScheduleInterval(endDateTime);

        //Required data - each doctor is matched with set of scheduleIntervals - time, when (s)he is working (not busy)
        Set<DoctorScheduleAssignmentCalendarData> doctorScheduleTableDataSet = schedules.stream().map(schedule ->
                new DoctorScheduleAssignmentCalendarData(schedule.getRelatedDoctor().getId(), "ABC",
                        schedule.getRelatedDoctor().getName(), schedule.getStateSet().subSet(intervalStart, intervalEnd)))
                .collect(Collectors.toSet());
                /*.collect(
                Collectors.toSet(
                        schedule -> new DoctorData(schedule.getRelatedDoctor().getId(), "ABC",
                                    schedule.getRelatedDoctor().getName(), schedule.getStateSet().subSet(intervalStart, intervalEnd))
                )
        );*/

        if (getFreeTimeOnly)
        {
            //Remove all assigned intervals
            for (var doctorData : doctorScheduleTableDataSet)
            {
                doctorData.getIntervalSet().removeIf(ScheduleInterval::isAssigned);
            }

            //Remove all entries that have empty value
            doctorScheduleTableDataSet.removeIf(doctorScheduleTableData -> doctorScheduleTableData.getIntervalSet().size() == 0);
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
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
                return new JsonPrimitive(dateTimeFormatter.format(localDateTime));
            }
        });

        Gson gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

        return gson.toJson(doctorScheduleTableDataSet);
    }
}
