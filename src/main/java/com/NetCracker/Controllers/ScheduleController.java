package com.NetCracker.Controllers;

import com.NetCracker.Entities.Doctor;
import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.NetCracker.Entities.Schedule.SchedulePattern;
import com.NetCracker.Services.SchedulePatternFactory;
import com.NetCracker.Services.ScheduleService;
import com.NetCracker.Services.ScheduleViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
public class ScheduleController
{
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleViewService scheduleViewService;

    @GetMapping("/schedule")
    public ResponseEntity<String> presentAllSchedules(@RequestParam(name = "doctorIds", required = false) Long[] doctorIds,
                                                      @RequestParam(name = "startDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
                                                      @RequestParam(name = "endDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
                                                      @RequestParam(name = "getFreeTimeOnly", required = false) Boolean getFreeTimeOnly)
    {
        System.out.println("/schedule");
        //If no array provided return all doctors' info
        if (doctorIds == null)
        {
            try
            {
                var debug = scheduleService.getAllDoctors();
                doctorIds = scheduleService.getAllDoctors().stream().map(Doctor::getId).toArray(Long[]::new);
            }
            catch (DataAccessException e)
            {
                return new ResponseEntity<String>(
                        "Server could not retrieve information from database (all doctor Ids)",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        //If no startDateTime provided use all available data by default
        if (startDateTime == null)
        {
            startDateTime = LocalDateTime.MIN;
        }

        //If no endDateTime provided use all available data by default
        if (endDateTime == null)
        {
            endDateTime = LocalDateTime.MAX;
        }

        //If no specifier provided show when doctors do their jobs
        if (getFreeTimeOnly == null)
        {
            getFreeTimeOnly = false;
        }

        String jsonTable = "";

        try
        {
            jsonTable = scheduleViewService.getScheduleTableJson(doctorIds, startDateTime, endDateTime, getFreeTimeOnly);
        }
        catch (DataAccessException | IllegalStateException e)
        {
            return new ResponseEntity<String>("Server could not retrieve information from database", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        System.out.println(jsonTable);
        return new ResponseEntity<String>(jsonTable, HttpStatus.OK);
    }
}
