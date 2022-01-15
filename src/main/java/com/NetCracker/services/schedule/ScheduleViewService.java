package com.NetCracker.services.schedule;

import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.doctor.Specialist;
import com.NetCracker.entities.schedule.DoctorSchedule;
import com.NetCracker.entities.schedule.scheduleElements.ScheduleInterval;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service contains methods that correspond to doctor schedule entity.
 * They are used to transferring data to/from (angular) client.
 */
@Service
public class ScheduleViewService
{
    @Autowired
    ScheduleService scheduleService;

    static class DoctorScheduleTableDataDaily
    {
        LocalDate date;

        String startOfDay;

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
        Long id;

        List<String> specializationNames;

        String firstName;

        String lastName;

        String middleName;

        SortedSet<DoctorScheduleTableDataDaily> dailyInformation;

        public DoctorScheduleTableData(DoctorSchedule schedule, LocalDateTime dateBeginRepresent, LocalDateTime dateEndRepresent)
        {
            this.id = schedule.getRelatedDoctor().getId();
            //this.specializationNames = "Goose";
            this.specializationNames = schedule.getRelatedDoctor().getSpecialist().stream().map(Specialist::getSpecialization).collect(Collectors.toList());
            this.firstName = schedule.getRelatedDoctor().getUser().getFirstName();
            this.lastName = schedule.getRelatedDoctor().getUser().getLastName();
            this.middleName = schedule.getRelatedDoctor().getUser().getPatronymic();
            this.dailyInformation = new TreeSet<DoctorScheduleTableDataDaily>(DoctorScheduleTableDataDaily.dateAscendComparator);
            var setIterator = schedule.getStateSet().iterator();

            LocalDate curDate = null;
            LocalTime startOfDay = null;
            LocalTime endOfDay = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            while (setIterator.hasNext())
            {
                var interval = setIterator.next();

                if (interval.getIntervalStartTime().isBefore(dateBeginRepresent))
                {
                    continue;
                }

                if (interval.getIntervalStartTime().isAfter(dateEndRepresent))
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
    public String getScheduleTableJson(List<Long> doctorIds, LocalDateTime dateBeginRepresent, LocalDateTime dateEndRepresent)
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

        Gson gson = gsonBuilder.create();

        return gson.toJson(doctorScheduleTableDataSet);
    }

    static class DoctorScheduleAssignmentCalendarData
    {
        //TODO - refactor name and specialization when doctor entity will release
        @Expose
        Long id;

        @Expose
        List<String> specializationNames;

        @Expose
        String firstName;

        @Expose
        String lastName;

        @Expose
        String middleName;

        @Expose
        SortedSet<ScheduleInterval> intervalCollection;

        public DoctorScheduleAssignmentCalendarData(Long id, List<String> specializationNames, String firstName, String lastName,
                                                    String middleName, SortedSet<ScheduleInterval> intervalSet)
        {
            this.id = id;
            this.specializationNames = specializationNames;
            this.firstName = firstName;
            this.lastName = lastName;
            this.middleName = middleName;
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
            new DoctorScheduleAssignmentCalendarData(schedule.getRelatedDoctor().getId(),
                    schedule.getRelatedDoctor().getSpecialist().stream().map(Specialist::getSpecialization).toList(),
                    schedule.getRelatedDoctor().getUser().getFirstName(),
                    schedule.getRelatedDoctor().getUser().getLastName(),
                    schedule.getRelatedDoctor().getUser().getPatronymic(),
                    schedule.getStateSet().subSet(intervalStart, intervalEnd)))
                .collect(Collectors.toSet());

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
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                return new JsonPrimitive(dateTimeFormatter.format(localDateTime.atOffset(ZoneOffset.UTC)));
            }
        });

        Gson gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

        return gson.toJson(doctorScheduleTableDataSet);
    }

    //Instances of this class are not managed by hibernate, so they can be serialized via Gson
    static class DoctorShortInformation
    {
        Long id;
        String firstName;
        String middleName;
        String lastName;
        Set<String> specializationNames;

        DoctorShortInformation(Doctor persistedShortInformation)
        {
            this.id = persistedShortInformation.getId();
            this.firstName = persistedShortInformation.getUser().getFirstName();
            this.middleName = persistedShortInformation.getUser().getPatronymic();
            this.lastName = persistedShortInformation.getUser().getLastName();
            if (persistedShortInformation.getSpecialist() == null)
            {
                this.specializationNames = new HashSet<>();
            }
            else
            {
                this.specializationNames = persistedShortInformation.getSpecialist().stream()
                        .map(Specialist::getSpecialization)
                        .collect(Collectors.toSet());
            }
        }
    }

    public String getDoctorsShortInformationJson(List<Long> doctorIds)
    {
        //TODO - switch to related service, delete function.
        var persistedDoctorShortInformationCollection = scheduleService.getDoctorShortInformation(doctorIds);
        for (var debug : persistedDoctorShortInformationCollection)
        {
            System.out.println("Geese are cool!");
        }
        var doctorShortInformationCollection = new ArrayList<>();
        persistedDoctorShortInformationCollection.forEach(persistedPair ->
                doctorShortInformationCollection.add(new DoctorShortInformation(persistedPair)));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(doctorShortInformationCollection);
    }
}
