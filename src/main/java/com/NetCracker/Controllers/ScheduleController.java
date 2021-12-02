package com.NetCracker.Controllers;

import com.NetCracker.Entities.DoctorStub;
import com.NetCracker.Entities.Schedule.SchedulePattern;
import com.NetCracker.Services.DoctorStubService;
import com.NetCracker.Services.SchedulePatternService;
import com.NetCracker.Services.ScheduleService;
import com.NetCracker.Services.ScheduleViewService;
import com.google.gson.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ScheduleController
{
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleViewService scheduleViewService;

    @Autowired
    private SchedulePatternService schedulePatternService;

    @Autowired
    private DoctorStubService doctorStubService;

    String prepareDateStringToParse(String dateStr)
    {
        if (dateStr.charAt(0) == ' ')
        {
            StringBuilder builder = new StringBuilder(dateStr);
            builder.setCharAt(0, '+');
            return builder.toString();
        }
        return dateStr;
    }

    List<Long> stringToLongList(String str) throws NumberFormatException
    {
        try
        {
            return Arrays.stream(str.split(",")).map(Long::valueOf).collect(Collectors.toList());
        }
        catch(NumberFormatException e)
        {
            return null;
        }
    }

    LocalDateTime stringToDateTime(String str)
    {
        DateTimeFormatter ISOFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        try
        {
            return ZonedDateTime.parse(prepareDateStringToParse(str), ISOFormatter).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        }
        catch (DateTimeParseException e)
        {
            return null;
        }
    }

    /**
     * Input - 2021-12-27T21:00:00.000Z, output - 2021-12-27T21:00 (if system time is Moscow)
     * @param str - ISO representation of date
     * @return
     */
    LocalDateTime stringToLocalDateTime(String str)
    {
        DateTimeFormatter ISOFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        try
        {
            return LocalDateTime.parse(prepareDateStringToParse(str), ISOFormatter);
        }
        catch (DateTimeParseException e)
        {
            return null;
        }
    }

    @GetMapping("/schedule/table")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> getDataForTable(@RequestParam(name = "doctorIds", required = false) String doctorIdsStr,
                                                  @RequestParam(name = "dateBeginRepresent", required = false) String dateBeginRepresentStr,
                                                  @RequestParam(name = "dateEndRepresent", required = false) String dateEndRepresentStr)
    {
        List<Long> doctorIds = new ArrayList<>();

        LocalDateTime dateBeginRepresent = null;
        LocalDateTime dateEndRepresent = null;

        //If no array provided return all doctors' info
        if (doctorIdsStr == null)
        {
            try
            {
                doctorIds = scheduleService.getAllDoctors().stream().map(DoctorStub::getId).collect(Collectors.toList());
            }
            catch (DataAccessException e)
            {
                //TODO - log errors
                return new ResponseEntity<String>(
                        "Server could not retrieve information from database (all doctor Ids)",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
        {
            doctorIds = stringToLongList(doctorIdsStr);
            if (doctorIds == null)
            {
                return new ResponseEntity<String>("Invalid request - doctorIds is a malformed string representation", HttpStatus.BAD_REQUEST);
            }
        }

        //If no startDateTime provided use all available data by default
        if (dateBeginRepresentStr == null)
        {
            dateBeginRepresent = LocalDateTime.MIN;
        }
        else
        {
            dateBeginRepresent = stringToLocalDateTime(dateBeginRepresentStr);
            if (dateBeginRepresent == null)
            {
                return new ResponseEntity<String>("Invalid request - dateBeginRepresent - could not parse temporal value", HttpStatus.BAD_REQUEST);
            }
        }

        //If no endDateTime provided use all available data by default
        if (dateEndRepresentStr == null)
        {
            dateEndRepresent = LocalDateTime.MAX;
        }
        else
        {
            dateEndRepresent = stringToLocalDateTime(dateEndRepresentStr);
            if (dateEndRepresent == null)
            {
                return new ResponseEntity<String>("Invalid request - dateEndRepresent - could not parse temporal value", HttpStatus.BAD_REQUEST);
            }
        }

        String jsonTable = "";
        try
        {
            jsonTable = scheduleViewService.getScheduleTableJson(doctorIds, dateBeginRepresent, dateEndRepresent.plusDays(1));
        }
        catch (DataAccessException | IllegalStateException e)
        {
            return new ResponseEntity<String>("Server could not retrieve information from database or serialize it", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //TODO - delete in prod
        System.out.println(jsonTable);

        return new ResponseEntity<String>(jsonTable, HttpStatus.OK);
    }

    @GetMapping("/schedule/calendar")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> getAvailableAppointmentsTime(@RequestParam(name = "doctorIds", required = false) String doctorIdsStr,
                                                               @RequestParam(name = "startDate", required = false) String startDateStr,
                                                               @RequestParam(name = "endDate", required = false) String endDateStr,
                                                               @RequestParam(name = "getFreeTimeOnly", required = false) Boolean getFreeTimeOnly)
    {
        List<Long> doctorIds = new ArrayList<>();

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        System.out.println("/schedule");
        //If no array provided return all doctors' info
        if (doctorIdsStr == null)
        {
            try
            {
                var debug = scheduleService.getAllDoctors();
                doctorIds = scheduleService.getAllDoctors().stream().map(DoctorStub::getId).toList();
            }
            catch (DataAccessException e)
            {
                //TODO - log errors
                return new ResponseEntity<String>(
                        "Server could not retrieve information from database - IDs of all doctors",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
        {
            doctorIds = stringToLongList(doctorIdsStr);
            if (doctorIds == null)
            {
                return new ResponseEntity<String>("Invalid request - doctorIds is a malformed string representation", HttpStatus.BAD_REQUEST);
            }
        }

        //If no startDateTime provided use all available data by default
        if (startDateStr == null)
        {
            startDate = LocalDateTime.MIN;
        }
        else
        {
            startDate = stringToLocalDateTime(startDateStr);
            if (startDate == null)
            {
                return new ResponseEntity<String>("Invalid request - startDate - could not parse temporal value", HttpStatus.BAD_REQUEST);
            }
        }

        //If no endDateTime provided use all available data by default
        if (endDateStr == null)
        {
            endDate = startDate.plusDays(7);
        }
        else
        {
            endDate = stringToLocalDateTime(endDateStr);
            if (endDate == null)
            {
                return new ResponseEntity<String>("Invalid request - startDate - could not parse temporal value", HttpStatus.BAD_REQUEST);
            }

            if (endDate.isBefore(startDate))
            {
                return new ResponseEntity<String>("Invalid request - endDate is before startDate", HttpStatus.BAD_REQUEST);
            }
        }

        //If no specifier provided show when doctors do their jobs
        if (getFreeTimeOnly == null)
        {
            getFreeTimeOnly = false;
        }

        String jsonCalendar = "";

        try
        {
            jsonCalendar = scheduleViewService.getScheduleAssignmentCalendarJson(doctorIds, startDate, endDate, getFreeTimeOnly);
        }
        catch (DataAccessException | IllegalStateException e)
        {
            //TODO - log errors
            return new ResponseEntity<String>(
                    "Server could not retrieve information from database or serialize it - getting data for calendar",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Throwable e)
        {
            return new ResponseEntity<String>("Unknown error - retry later", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //TODO - delete in prod
        System.out.println(jsonCalendar);

        return new ResponseEntity<String>(jsonCalendar, HttpStatus.OK);
    }

    @GetMapping("/schedule/calendar/getDoctorNames")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> getDoctorNames(@RequestParam(name = "doctorIds", required = false) List<Long> doctorIds)
    {
        if (doctorIds == null)
        {
            doctorIds = scheduleService.getAllDoctors().stream().map(DoctorStub::getId).toList();
        }

        String doctorsShortInformation = null;
        try
        {
            doctorsShortInformation = scheduleViewService.getDoctorsShortInformation(doctorIds);
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>(
                    "Server could not retrieve information from database - getting doctor name and id",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(doctorsShortInformation, HttpStatus.OK);
    }

    @PostMapping("/schedule/add-pattern")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> addSchedulePattern(@RequestBody Map<String,Object> responseBody)
    {
        List<Object> updates;
        try
        {
            var bodyParameters = (Map<String, Object>) responseBody.get("params");
            updates = (List<Object>) bodyParameters.get("updates");
        }
        catch (ClassCastException e)
        {
            return new ResponseEntity<String>("Could not parse request body", HttpStatus.BAD_REQUEST);
        }

        if (updates.size() != 1)
        {
            return new ResponseEntity<String>("Too many parameters!", HttpStatus.BAD_REQUEST);
        }

        Map<String, String> parameter;
        try
        {
            parameter = (Map<String, String>) updates.get(0);
        }
        catch (ClassCastException e)
        {
            return new ResponseEntity<String>("Could not parse request body", HttpStatus.BAD_REQUEST);
        }

        if (!parameter.get("param").equals("schedulePattern"))
        {
            return new ResponseEntity<String>("Invalid input data. Could not parse request body parameter", HttpStatus.BAD_REQUEST);
        }
        String schedulePattern = parameter.get("value");

        SchedulePattern newSchedulePattern;
        try
        {
            newSchedulePattern = schedulePatternService.fromJson(schedulePattern);
        }
        catch (JsonParseException e)
        {
            return new ResponseEntity<String>("Invalid input data. Could not parse schedule pattern", HttpStatus.BAD_REQUEST);
        }

        if (newSchedulePattern == null)
        {
            return new ResponseEntity<String>("Invalid input data. Could not find related doctor", HttpStatus.BAD_REQUEST);
        }

        try
        {
            schedulePatternService.save(newSchedulePattern);
        }
        catch (ConstraintViolationException e)
        {
            return new ResponseEntity<String>("Could not save schedule pattern - name is not unique", HttpStatus.BAD_REQUEST);
        }
        catch(DataAccessException e)
        {
            return new ResponseEntity<String>("Could not save schedule pattern to database", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    @GetMapping("/schedule/list-patterns")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> getSchedulePatternList()
    {
        String schedulePatternList;
        try
        {
            schedulePatternList = scheduleViewService.getSchedulePatternList();
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("Could not extract pattern names from database!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(schedulePatternList, HttpStatus.OK);
    }

    @PatchMapping("/schedule/apply-pattern")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> prolongSchedule(@RequestBody Map<String, Object> requestBody)
    {
        String patternName;
        String dateToApplyStr;
        try
        {
            patternName = (String) requestBody.get("patternName");
            dateToApplyStr = (String) requestBody.get("dateToApplyStr");
        }
        catch (ClassCastException | NullPointerException e)
        {
            return new ResponseEntity<String>("Invalid request - wrong parameters", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime dateToApply = stringToDateTime(dateToApplyStr);
        if (dateToApply == null)
        {
            return new ResponseEntity<String>("Invalid request - dateToApply - could not parse temporal value", HttpStatus.BAD_REQUEST);
        }

        //TODO - get current doctor - finish when spring security done
        DoctorStub doctor = doctorStubService.getDoctorById(1);
        SchedulePattern requestedPattern;

        try
        {
            requestedPattern = schedulePatternService.findPatternByName(patternName);
        }
        catch(DataAccessException e)
        {
            return new ResponseEntity<String>("Pattern with specified name does not exist", HttpStatus.BAD_REQUEST);
        }

        try
        {
            scheduleService.applyPatternToSchedule(doctor, requestedPattern, dateToApply.toLocalDate());
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("Could not prolong schedule", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    @GetMapping("/test")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> testMapping()
    {
        String response = "{Geese are cool!}";
        System.out.println(response);
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }
}
