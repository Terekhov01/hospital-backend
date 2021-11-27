package com.NetCracker.Controllers;

import com.NetCracker.Entities.Doctor;
import com.NetCracker.Services.ScheduleService;
import com.NetCracker.Services.ScheduleViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleViewService scheduleViewService;

    String prepareDateStringToParse(String dateStr) {
        if (dateStr.charAt(0) == ' ') {
            StringBuilder builder = new StringBuilder(dateStr);
            builder.setCharAt(0, '+');
            return builder.toString();
        }
        return dateStr;
    }

    List<Long> stringToLongList(String str) throws NumberFormatException {
        try {
            return Arrays.stream(str.split(",")).map(Long::valueOf).collect(Collectors.toList());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    LocalDate stringToLocalDate(String str) {
        DateTimeFormatter ISOFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        try {
            return LocalDateTime.parse(prepareDateStringToParse(str), ISOFormatter).toLocalDate();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @GetMapping("/schedule/table")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> getDataForTable(@RequestParam(name = "doctorIds", required = false) String doctorIdsStr,
                                                  @RequestParam(name = "dateBeginRepresent", required = false) String dateBeginRepresentStr,
                                                  @RequestParam(name = "dateEndRepresent", required = false) String dateEndRepresentStr
    ) {
        System.out.println("in get method");
        System.out.println("Begin date: " + dateEndRepresentStr != null ? dateEndRepresentStr : "null");
        List<Long> doctorIds = new ArrayList<>();

        LocalDate dateBeginRepresent = null;
        LocalDate dateEndRepresent = null;

        //If no array provided return all doctors' info
        if (doctorIdsStr == null) {
            try {
                doctorIds = scheduleService.getAllDoctors().stream().map(Doctor::getId).collect(Collectors.toList());
            } catch (DataAccessException e) {
                //TODO - log errors
                return new ResponseEntity<String>(
                        "Server could not retrieve information from database (all doctor Ids)",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            doctorIds = stringToLongList(doctorIdsStr);
            if (doctorIds == null) {
                return new ResponseEntity<String>("Invalid request - doctorIds is a malformed string representation", HttpStatus.BAD_REQUEST);
            }
        }

        //If no startDateTime provided use all available data by default
        if (dateBeginRepresentStr == null) {
            dateBeginRepresent = LocalDate.MIN;
        } else {
            dateBeginRepresent = stringToLocalDate(dateBeginRepresentStr);
            if (dateBeginRepresent == null) {
                return new ResponseEntity<String>("Invalid request - dateBeginRepresent - could not parse temporal value", HttpStatus.BAD_REQUEST);
            }
        }

        //If no endDateTime provided use all available data by default
        if (dateEndRepresentStr == null) {
            dateEndRepresent = LocalDate.MAX;
        } else {
            dateEndRepresent = stringToLocalDate(dateEndRepresentStr);
            if (dateEndRepresent == null) {
                return new ResponseEntity<String>("Invalid request - dateEndRepresent - could not parse temporal value", HttpStatus.BAD_REQUEST);
            }
        }

        String jsonTable = "";
        try {
            jsonTable = scheduleViewService.getScheduleTableJson(doctorIds, dateBeginRepresent, dateEndRepresent);
        } catch (DataAccessException | IllegalStateException e) {
            return new ResponseEntity<String>("Server could not retrieve information from database or serialize it", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //TODO - delete in prod
        System.out.println(jsonTable);

        return new ResponseEntity<String>(jsonTable, HttpStatus.OK);
    }

    @GetMapping("/schedule/calendar")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> getDataForCalendar(@RequestParam(name = "doctorIds", required = false) String doctorIdsStr,
                                                     @RequestParam(name = "startDate", required = false) String startDateStr,
                                                     @RequestParam(name = "endDate", required = false) String endDateStr,
                                                     @RequestParam(name = "getFreeTimeOnly", required = false) Boolean getFreeTimeOnly) {
        List<Long> doctorIds = new ArrayList<>();

        LocalDate startDate = null;
        LocalDate endDate = null;

        System.out.println("/schedule");
        //If no array provided return all doctors' info
        if (doctorIdsStr == null) {
            try {
                var debug = scheduleService.getAllDoctors();
//                doctorIds = scheduleService.getAllDoctors().stream().map(Doctor::getId).toList();
                doctorIds = scheduleService.getAllDoctors().stream().map(Doctor::getId).collect(Collectors.toList());
            } catch (DataAccessException e) {
                //TODO - log errors
                return new ResponseEntity<String>(
                        "Server could not retrieve information from database - IDs of all doctors",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            doctorIds = stringToLongList(doctorIdsStr);
            if (doctorIds == null) {
                return new ResponseEntity<String>("Invalid request - doctorIds is a malformed string representation", HttpStatus.BAD_REQUEST);
            }
        }

        //If no startDateTime provided use all available data by default
        if (startDateStr == null) {
            startDate = LocalDate.MIN;
        } else {
            startDate = stringToLocalDate(startDateStr);
            if (startDate == null) {
                return new ResponseEntity<String>("Invalid request - startDate - could not parse temporal value", HttpStatus.BAD_REQUEST);
            }
        }

        //If no endDateTime provided use all available data by default
        if (endDateStr == null) {
            endDate = startDate.plusDays(7);
        } else {
            endDate = stringToLocalDate(endDateStr);
            if (endDate == null) {
                return new ResponseEntity<String>("Invalid request - startDate - could not parse temporal value", HttpStatus.BAD_REQUEST);
            }

            if (endDate.isBefore(startDate)) {
                return new ResponseEntity<String>("Invalid request - endDate is before startDate", HttpStatus.BAD_REQUEST);
            }
        }

        //If no specifier provided show when doctors do their jobs
        if (getFreeTimeOnly == null) {
            getFreeTimeOnly = false;
        }

        String jsonCalendar = "";

        try {
            jsonCalendar = scheduleViewService.getScheduleAssignmentCalendarJson(doctorIds, startDate.atStartOfDay(), endDate.atStartOfDay(), getFreeTimeOnly);
        } catch (DataAccessException | IllegalStateException e) {
            //TODO - log errors
            return new ResponseEntity<String>(
                    "Server could not retrieve information from database or serialize it - getting data for calendar",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Throwable e) {
            return new ResponseEntity<String>("Unknown error - retry later", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //TODO - delete in prod
        System.out.println(jsonCalendar);

        return new ResponseEntity<String>(jsonCalendar, HttpStatus.OK);
    }

    @GetMapping("/schedule/calendar/getDoctorNames")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> getDoctorNames(@RequestParam(name = "doctorIds", required = false) List<Long> doctorIds) {
        if (doctorIds == null) {
//            doctorIds = scheduleService.getAllDoctors().stream().map(Doctor::getId).toList();
            doctorIds = scheduleService.getAllDoctors().stream().map(Doctor::getId).collect(Collectors.toList());
        }

        String doctorsShortInformation = null;
        try {
            doctorsShortInformation = scheduleViewService.getDoctorsShortInformation(doctorIds);
        } catch (DataAccessException e) {
            return new ResponseEntity<String>(
                    "Server could not retrieve information from database - getting doctor name and id",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        System.out.println(doctorsShortInformation);

        return new ResponseEntity<String>(doctorsShortInformation, HttpStatus.OK);
    }

    @GetMapping("/test")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> testMapping() {
        String response = "{Geese are cool!}";
        System.out.println(response);
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }
}
