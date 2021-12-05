package com.NetCracker.Controllers;

import com.NetCracker.Entities.DoctorStub;
import com.NetCracker.Services.ScheduleService;
import com.NetCracker.Services.ScheduleViewService;
import com.NetCracker.Utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("schedule")
@RestController
public class ScheduleController
{
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleViewService scheduleViewService;

    @PreAuthorize("permitAll()")
    @GetMapping("/table")
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
            doctorIds = StringUtils.stringToLongList(doctorIdsStr);
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
            dateBeginRepresent = StringUtils.stringToLocalDateTime(dateBeginRepresentStr);
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
            dateEndRepresent = StringUtils.stringToLocalDateTime(dateEndRepresentStr);
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

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/calendar")
    public ResponseEntity<String> getAvailableAppointmentsTime(@RequestParam(name = "doctorIds", required = false) String doctorIdsStr,
                                                               @RequestParam(name = "startDate", required = false) String startDateStr,
                                                               @RequestParam(name = "endDate", required = false) String endDateStr,
                                                               @RequestParam(name = "getFreeTimeOnly", required = false) Boolean getFreeTimeOnly)
    {
        List<Long> doctorIds = new ArrayList<>();

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

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
            doctorIds = StringUtils.stringToLongList(doctorIdsStr);
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
            startDate = StringUtils.stringToLocalDateTime(startDateStr);
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
            endDate = StringUtils.stringToLocalDateTime(endDateStr);
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
    
    @PreAuthorize("permitAll()")
    @GetMapping("/getDoctorNames")
    public ResponseEntity<String> getDoctorsShortInfo(@RequestParam(name = "doctorIds", required = false) List<Long> doctorIds)
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
}
