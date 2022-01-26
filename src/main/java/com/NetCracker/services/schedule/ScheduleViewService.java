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
import java.util.function.Supplier;
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

        SortedSet<LocalTime> timeIntervals;

        public DoctorScheduleTableDataDaily()
        {}

        public DoctorScheduleTableDataDaily(LocalDate date, SortedSet<LocalTime> timeIntervals)
        {
            this.date = date;
            this.timeIntervals = timeIntervals;
        }

        public LocalDate getDate()
        {
            return date;
        }

        public SortedSet<LocalTime> getTimeIntervals()
        {
            return timeIntervals;
        }

        public static final Comparator<DoctorScheduleTableDataDaily> dateAscendComparator = Comparator.comparing(DoctorScheduleTableDataDaily::getDate);
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
            this.specializationNames = schedule.getRelatedDoctor().getSpecialist().stream().map(Specialist::getSpecialization).collect(Collectors.toList());
            this.firstName = schedule.getRelatedDoctor().getUser().getFirstName();
            this.lastName = schedule.getRelatedDoctor().getUser().getLastName();
            this.middleName = schedule.getRelatedDoctor().getUser().getPatronymic();
            this.dailyInformation = new TreeSet<>(DoctorScheduleTableDataDaily.dateAscendComparator);
            var subSet = schedule.getStateSet().subSet(new ScheduleInterval(dateBeginRepresent), new ScheduleInterval(dateEndRepresent));

            LocalDate curDate = null;
            DoctorScheduleTableDataDaily dayData = null;

            for (var interval : subSet)
            {
                if (interval.getStartTime().toLocalDate().equals(curDate))
                {
                    dayData.getTimeIntervals().add(interval.getStartTime().toLocalTime());
                }
                else
                {
                    if (dayData != null)
                    {
                        dailyInformation.add(dayData);
                    }

                    curDate = interval.getStartTime().toLocalDate();
                    dayData = new DoctorScheduleTableDataDaily(curDate, new TreeSet<>(Comparator.naturalOrder()));
                    dayData.getTimeIntervals().add(interval.getStartTime().toLocalTime());
                }
            }

            if (dayData != null && dayData.getTimeIntervals().size() != 0)
            {
                dailyInformation.add(dayData);
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

        Supplier<TreeSet<DoctorScheduleTableData>> treeSetSupplier = () -> new TreeSet<>(new Comparator<DoctorScheduleTableData>()
        {
            @Override
            public int compare(DoctorScheduleTableData o1, DoctorScheduleTableData o2)
            {
                int lastNameComparisonResult = o1.lastName.compareTo(o2.lastName);
                if (lastNameComparisonResult != 0)
                {
                    return lastNameComparisonResult;
                }

                int firstNameComparisonResult = o1.firstName.compareTo(o2.firstName);

                if (firstNameComparisonResult != 0)
                {
                    return firstNameComparisonResult;
                }

                int middleNameComparisonResult = o1.middleName.compareTo(o2.middleName);

                if (middleNameComparisonResult != 0)
                {
                    return middleNameComparisonResult;
                }

                return o1.id.compareTo(o2.id);
            }
        });
        //Required data - each doctor is matched with set of scheduleIntervals - time, when (s)he is working (not busy)
        SortedSet<DoctorScheduleTableData> doctorScheduleTableDataSet = schedules.stream()
                .map(schedule -> new DoctorScheduleTableData(schedule, dateBeginRepresent, dateEndRepresent))
                .collect(Collectors.toCollection(treeSetSupplier));

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

        gsonBuilder.registerTypeAdapter(LocalTime.class, new JsonSerializer<LocalTime>()
        {
            @Override
            public JsonElement serialize(LocalTime localTime, Type type, JsonSerializationContext jsonDeserializationContext) throws JsonParseException
            {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                return new JsonPrimitive(formatter.format(localTime));
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
        var doctorShortInformationCollection = new ArrayList<>();
        persistedDoctorShortInformationCollection.forEach(persistedPair ->
                doctorShortInformationCollection.add(new DoctorShortInformation(persistedPair)));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(doctorShortInformationCollection);
    }
}
