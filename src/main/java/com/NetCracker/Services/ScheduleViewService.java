package com.NetCracker.Services;

import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;
import com.NetCracker.Entities.Schedule.SchedulePattern;
import com.NetCracker.Repositories.DoctorRepository;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;
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

    @Autowired
    SchedulePatternService schedulePatternService;

    static class DoctorScheduleTableDataDaily
    {
        @Expose
        LocalDate date;

        @Expose
        String startOfDay;

        @Expose
        String endOfDay;

        public DoctorScheduleTableDataDaily(LocalDate date, String startOfDay, String endOfDay)
        {
            this.date = date;
            this.startOfDay = startOfDay;
            this.endOfDay = endOfDay;
        }

        public LocalDate getDate()
        {
            return date;
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
        String specializationName;

        @Expose
        String doctorName;

        @Expose
        SortedSet<DoctorScheduleTableDataDaily> dailyInformation;

        public DoctorScheduleTableData(DoctorSchedule schedule, LocalDate dateBeginRepresent, LocalDate dateEndRepresent)
        {
            this.id = schedule.getRelatedDoctor().getId();
            this.specializationName = "Goose";
            //this.specialization = schedule.getRelatedDoctor().getSpecialization().toString();
            this.doctorName = schedule.getRelatedDoctor().getName();
            this.dailyInformation = new TreeSet<DoctorScheduleTableDataDaily>(DoctorScheduleTableDataDaily.dateAscendComparator);
            var setIterator = schedule.getStateSet().iterator();

            LocalDate curDate = null;
            LocalTime startOfDay = null;
            LocalTime endOfDay = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            while (setIterator.hasNext())
            {
                var interval = setIterator.next();

                if (interval.getIntervalStartTime().isBefore(dateBeginRepresent.atStartOfDay()))
                {
                    continue;
                }

                if (interval.getIntervalStartTime().isAfter(dateEndRepresent.plusDays(1).atStartOfDay()))
                {
                    break;
                }

                LocalDate intervalDate = interval.getIntervalStartTime().toLocalDate();
                LocalTime intervalTime = interval.getIntervalStartTime().toLocalTime();
                if (intervalDate.equals(curDate))
                {
                    if (intervalTime.plusMinutes(30).isAfter(endOfDay))
                    {
                        endOfDay = intervalTime.plusMinutes(30);
                    }
                }
                else
                {
                    if (curDate != null)
                    {
                        dailyInformation.add(new DoctorScheduleTableDataDaily(curDate, startOfDay.format(formatter), endOfDay.format(formatter)));
                    }

                    curDate = intervalDate;
                    startOfDay = intervalTime;
                    endOfDay = intervalTime.plusMinutes(30);
                }
            }
            if (startOfDay != null && endOfDay != null)
            {
                dailyInformation.add(new DoctorScheduleTableDataDaily(curDate, startOfDay.format(formatter), endOfDay.format(formatter)));
            }
        }

        public Set<DoctorScheduleTableDataDaily> getIntervalSet()
        {
            return dailyInformation;
        }
    }

    /**
     *
     * @param doctorIds ids of doctors whose data to obtain.
     * @return Json string that contains requested data in such format that it can be parsed by Angular.
     * Returned data is presented in material table.
     */
    public String getScheduleTableJson(List<Long> doctorIds, LocalDate dateBeginRepresent, LocalDate dateEndRepresent)
    {
        Set<DoctorSchedule> schedules = scheduleService.getDoctorSchedule(doctorIds);

        //Required data - each doctor is matched with set of scheduleIntervals - time, when (s)he is working (not busy)
        Set<DoctorScheduleTableData> doctorScheduleTableDataSet = schedules.stream()
                .map(schedule -> new DoctorScheduleTableData(schedule, dateBeginRepresent, dateEndRepresent))
                .collect(Collectors.toSet());

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>()
        {
            @Override
            public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonDeserializationContext) throws JsonParseException
            {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;
                return new JsonPrimitive(dateTimeFormatter.format(localDate));
            }
        });

        Gson gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

        return gson.toJson(doctorScheduleTableDataSet);
    }


    static class DoctorScheduleAssignmentCalendarData
    {
        //TODO - refactor name and specialization when doctor entity will release
        @Expose
        Long id;

        @Expose
        String specializationName;

        @Expose
        String doctorName;

        @Expose
        SortedSet<ScheduleInterval> intervalCollection;

        public DoctorScheduleAssignmentCalendarData(Long id, String specializationName, String doctorName,
                                                    SortedSet<ScheduleInterval> intervalSet)
        {
            this.id = id;
            this.specializationName = specializationName;
            this.doctorName = doctorName;
            this.intervalCollection = intervalSet;
        }

        public Set<ScheduleInterval> getIntervalSet()
        {
            return intervalCollection;
        }
    }

    /**
     *
     * @param doctorIds ids of doctors whose information to obtain.
     * @param startDateTime all returned dates must be after specified date and time
     * @param endDateTime all returned dates must be before specified date and time
     * @param getFreeTimeOnly signals whether to return moments when doctors already have appointments
     * @return string that contains information about time intervals when doctors work and if they already have appointments
     */
    public String getScheduleAssignmentCalendarJson(List<Long> doctorIds, LocalDateTime startDateTime,
                                                    LocalDateTime endDateTime, Boolean getFreeTimeOnly)
    {
        Set<DoctorSchedule> schedules = scheduleService.getDoctorSchedule(doctorIds);

        ScheduleInterval intervalStart = new ScheduleInterval(startDateTime);
        ScheduleInterval intervalEnd = new ScheduleInterval(endDateTime);

        //Required data - each doctor is matched with set of scheduleIntervals - time, when (s)he is working (not busy)
        Set<DoctorScheduleAssignmentCalendarData> doctorScheduleTableDataSet = schedules.stream().map(schedule ->
            new DoctorScheduleAssignmentCalendarData(schedule.getRelatedDoctor().getId(), schedule.getRelatedDoctor().getSpecialization(),
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

    //Instances of this class are not managed by hibernate, so they can be serialized via Gson
    static class DoctorShortInformation
    {
        Long id;
        String doctorName;
        String specializationName;

        DoctorShortInformation(DoctorRepository.@NotNull DoctorShortInformation persistedShortInformation)
        {
            this.id = persistedShortInformation.getId();
            this.doctorName = persistedShortInformation.getName();
            this.specializationName = persistedShortInformation.getSpecialization();
        }
    }

    public String getDoctorsShortInformation(List<Long> doctorIds)
    {
        //TODO - switch to related service, delete function.
        var persistedDoctorShortInformationCollection = scheduleService.getDoctorShortInformation(doctorIds);
        var doctorShortInformationCollection = new ArrayList<>();
        persistedDoctorShortInformationCollection.forEach(persistedPair ->
                doctorShortInformationCollection.add(new DoctorShortInformation(persistedPair)));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(doctorShortInformationCollection);
    }

    public String getSchedulePatternList()
    {
        List<SchedulePattern> schedulePatternList = schedulePatternService.getPatternsByDoctor();

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
}
