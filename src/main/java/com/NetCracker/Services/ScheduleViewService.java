package com.NetCracker.Services;

import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;
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
public class ScheduleViewService {
    @Autowired
    ScheduleService scheduleService;

    static class DoctorScheduleTableDataDaily {
        @Expose
        LocalDate date;

        @Expose
        String startOfDay;

        @Expose
        String endOfDay;

        public DoctorScheduleTableDataDaily(LocalDate date, String startOfDay, String endOfDay) {
            this.date = date;
            this.startOfDay = startOfDay;
            this.endOfDay = endOfDay;
        }

        public LocalDate getDate() {
            return date;
        }

        public static final Comparator<DoctorScheduleTableDataDaily> dateAscendComparator = new Comparator<DoctorScheduleTableDataDaily>() {
            @Override
            public int compare(DoctorScheduleTableDataDaily o1, DoctorScheduleTableDataDaily o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        };
    }

    static class DoctorScheduleTableData {
        //TODO - refactor name and specialization when doctor entity will release
        @Expose
        Long id;

        @Expose
        String specializationName;

        @Expose
        String doctorName;

        @Expose
        SortedSet<DoctorScheduleTableDataDaily> dailyInformation;

        public DoctorScheduleTableData(DoctorSchedule schedule, LocalDate dateBeginRepresent, LocalDate dateEndRepresent) {
            this.id = schedule.getRelatedDoctor().getId();
            this.specializationName = "Goose";
            //this.specialization = schedule.getRelatedDoctor().getSpecialization().toString();
            this.doctorName = schedule.getRelatedDoctor().getLastName();
            this.dailyInformation = new TreeSet<DoctorScheduleTableDataDaily>(DoctorScheduleTableDataDaily.dateAscendComparator);
            var setIterator = schedule.getStateSet().iterator();

            LocalDate curDate = null;
            LocalTime startOfDay = null;
            LocalTime endOfDay = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            System.out.println("Constructing table data");
            while (setIterator.hasNext()) {
                System.out.println("Iterator has next");
                var interval = setIterator.next();
                System.out.println("Schedule interval: " + interval.getIntervalStartTime().toString());

                System.out.println("curDate: " + (Objects.isNull(curDate) ? "null" : curDate.toString()));

                if (interval.getIntervalStartTime().isBefore(dateBeginRepresent.atStartOfDay())) {
                    System.out.println("Continue");
                    continue;
                }

                if (interval.getIntervalStartTime().isAfter(dateEndRepresent.atStartOfDay())) {
                    System.out.println("Break");
                    break;
                }

                System.out.println("curDate: " + (Objects.isNull(curDate) ? "null" : curDate.toString()));

                LocalDate intervalDate = interval.getIntervalStartTime().toLocalDate();
                LocalTime intervalTime = interval.getIntervalStartTime().toLocalTime();

                System.out.println("Interval date: " + intervalDate.toString());
                System.out.println("Interval time: " + intervalTime.toString());

                System.out.println("curDate: " + (Objects.isNull(curDate) ? "null" : curDate.toString()));


                if (intervalDate.equals(curDate)) {
                    System.out.println("equals");
                    if (intervalTime.plusMinutes(30).isAfter(endOfDay)) {
                        System.out.println("Plus 30 minutes");
                        endOfDay = intervalTime.plusMinutes(30);
                    }
                } else {
                    System.out.println("In else clause");
                    if (curDate != null) {
                        System.out.println("DATE IS NOT NULL: " + startOfDay.format(formatter));
                        dailyInformation.add(new DoctorScheduleTableDataDaily(curDate, startOfDay.format(formatter), endOfDay.format(formatter)));
                    }

                    curDate = intervalDate;
                    startOfDay = intervalTime;
                    endOfDay = intervalTime.plusMinutes(30);
                    System.out.println("DATE IS: " + startOfDay.format(formatter));
                }
            }

        }

        public Set<DoctorScheduleTableDataDaily> getIntervalSet() {
            return dailyInformation;
        }
    }

    /**
     * @param doctorIds ids of doctors whose data to obtain.
     * @return Json string that contains requested data in such format that it can be parsed by Angular.
     * Returned data is presented in material table.
     */
    public String getScheduleTableJson(List<Long> doctorIds, LocalDate dateBeginRepresent, LocalDate dateEndRepresent) {
        Set<DoctorSchedule> schedules = scheduleService.getDoctorSchedule(doctorIds);

        System.out.println("DoctorIds: " + doctorIds.toString());
        System.out.println("schedules is empty? " + schedules.isEmpty());
        System.out.println("Begin Date is: " + dateBeginRepresent.toString());
        System.out.println("End Date is: " + dateEndRepresent.toString());

        //Required data - each doctor is matched with set of scheduleIntervals - time, when (s)he is working (not busy)
        Set<DoctorScheduleTableData> doctorScheduleTableDataSet = schedules.stream()
                .map(schedule -> new DoctorScheduleTableData(schedule, dateBeginRepresent, dateEndRepresent))
                .collect(Collectors.toSet());

        // testing part | remove later
        HashSet<DoctorScheduleTableData> tmp = new HashSet<>(doctorScheduleTableDataSet);
        Iterator<DoctorScheduleTableData> i = tmp.iterator();
        System.out.println("Daily info " + i.next().dailyInformation.toString()); //why empty:
        // testing part | remove later

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonDeserializationContext) throws JsonParseException {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;
                return new JsonPrimitive(dateTimeFormatter.format(localDate));
            }
        });

        Gson gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

        return gson.toJson(doctorScheduleTableDataSet);
    }


    static class DoctorScheduleAssignmentCalendarData {
        //TODO - refactor name and specialization when doctor entity will release
        @Expose
        Long id;

        @Expose
        String specializationName;

        @Expose
        String doctorName;

        @Expose
        SortedSet<ScheduleInterval> intervalCollection;

        public DoctorScheduleAssignmentCalendarData(Long id, String specializationName, String doctorName, SortedSet<ScheduleInterval> intervalSet) {
            this.id = id;
            this.specializationName = specializationName;
            this.doctorName = doctorName;
            this.intervalCollection = intervalSet;
        }

        public Set<ScheduleInterval> getIntervalSet() {
            return intervalCollection;
        }
    }

    /**
     * @param doctorIds       ids of doctors whose information to obtain.
     * @param startDateTime   all returned dates must be after specified date and time
     * @param endDateTime     all returned dates must be before specified date and time
     * @param getFreeTimeOnly signals whether to return moments when doctors already have appointments
     * @return string that contains information about time intervals when doctors work and if they already have appointments
     */
    public String getScheduleAssignmentCalendarJson(List<Long> doctorIds, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean getFreeTimeOnly) {
        Set<DoctorSchedule> schedules = scheduleService.getDoctorSchedule(doctorIds);

        ScheduleInterval intervalStart = new ScheduleInterval(startDateTime);
        ScheduleInterval intervalEnd = new ScheduleInterval(endDateTime);


        //Required data - each doctor is matched with set of scheduleIntervals - time, when (s)he is working (not busy)
        Set<DoctorScheduleAssignmentCalendarData> doctorScheduleTableDataSet = new HashSet<>();
        for (DoctorSchedule schedule : schedules) {
            Long docId = (long) schedule.getRelatedDoctor().getId();
            DoctorScheduleAssignmentCalendarData abc = new DoctorScheduleAssignmentCalendarData(docId, "ABC",
                    schedule.getRelatedDoctor().getLastName(), schedule.getStateSet().subSet(intervalStart, intervalEnd));
            doctorScheduleTableDataSet.add(abc);
        }
                /*.collect(
                Collectors.toSet(
                        schedule -> new DoctorData(schedule.getRelatedDoctor().getId(), "ABC",
                                    schedule.getRelatedDoctor().getName(), schedule.getStateSet().subSet(intervalStart, intervalEnd))
                )
        );*/

        if (getFreeTimeOnly) {
            //Remove all assigned intervals
            for (var doctorData : doctorScheduleTableDataSet) {
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
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonDeserializationContext) throws JsonParseException {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
                return new JsonPrimitive(dateTimeFormatter.format(localDateTime));
            }
        });

        Gson gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

        return gson.toJson(doctorScheduleTableDataSet);
    }

    //Instances of this class are not managed by hibernate, so they can be serialized via Gson
    static class DoctorShortInformation {
        Long id;
        String doctorName;
        String specializationName;

        DoctorShortInformation(DoctorRepository.@NotNull DoctorShortInformation persistedPair) {
            this.id = persistedPair.getId();
            this.doctorName = persistedPair.getLastName();
            this.specializationName = "ABC";
        }
    }

    public String getDoctorsShortInformation(List<Long> doctorIds) {
        //TODO - switch to related service, delete function.
        var persistedDoctorIdNameCollection = scheduleService.getDoctorShortInformation(doctorIds);
        var doctorIdNameCollection = new ArrayList<>();
        persistedDoctorIdNameCollection.forEach(persistedPair ->
                doctorIdNameCollection.add(new DoctorShortInformation(persistedPair)));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(doctorIdNameCollection);
    }
}

