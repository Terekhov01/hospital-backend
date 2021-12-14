package com.NetCracker.controllers.schedule;

import com.NetCracker.services.schedule.ScheduleService;
import com.NetCracker.services.schedule.ScheduleViewService;
import com.NetCracker.utils.StringUtils;
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
                doctorIds = scheduleService.getAllDoctorSchedules().stream().map(doctorSchedule -> doctorSchedule.getRelatedDoctor().getId()).collect(Collectors.toList());
            }
            catch (DataAccessException e)
            {
                //TODO - log errors
                return new ResponseEntity<String>(
                        "Сервер не смог загрузить информацию обо всех докторах из базы данных",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
        {
            doctorIds = StringUtils.stringToLongList(doctorIdsStr);
            if (doctorIds == null)
            {
                return new ResponseEntity<String>("Некорректный запрос - идентификаторы докторов получены в неверном формате", HttpStatus.BAD_REQUEST);
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
                return new ResponseEntity<String>("Некорректный запрос - не получилось распознать начальную дату", HttpStatus.BAD_REQUEST);
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
                return new ResponseEntity<String>("Некорректный запрос - не получилось распознать конечную дату", HttpStatus.BAD_REQUEST);
            }

            if (dateEndRepresent.isBefore(dateBeginRepresent))
            {
                return new ResponseEntity<String>("Некорректный запрос - начальная дата оказалась позже конечной", HttpStatus.BAD_REQUEST);
            }
        }

        //Protection from ddos attacks - the bigger date range is the longer it takes for server to process query
        if (dateBeginRepresent.plusDays(31).isBefore(dateEndRepresent))
        {
            dateEndRepresent = dateBeginRepresent.plusDays(31);
        }

        String jsonTable = "";
        try
        {
            jsonTable = scheduleViewService.getScheduleTableJson(doctorIds, dateBeginRepresent, dateEndRepresent.plusDays(1));
        }
        catch (DataAccessException | IllegalStateException e)
        {
            return new ResponseEntity<String>("Сервер не смог получить информацию из базы данных или обработать её", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //TODO - delete in prod
        System.out.println(jsonTable);

        return new ResponseEntity<String>(jsonTable, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
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
                doctorIds = scheduleService.getAllDoctorSchedules().stream().map(doctorSchedule -> doctorSchedule.getRelatedDoctor().getId()).toList();
            }
            catch (DataAccessException e)
            {
                //TODO - log errors
                return new ResponseEntity<String>(
                        "Сервер не смог загрузить информацию обо всех докторах из базы данных",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
        {
            doctorIds = StringUtils.stringToLongList(doctorIdsStr);
            if (doctorIds == null)
            {
                return new ResponseEntity<String>("Некорректный запрос - идентификаторы докторов получены в неверном формате", HttpStatus.BAD_REQUEST);
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
                return new ResponseEntity<String>("Некорректный запрос - не получилось распознать начальную дату", HttpStatus.BAD_REQUEST);
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
                return new ResponseEntity<String>("Некорректный запрос - не получилось распознать конечную дату", HttpStatus.BAD_REQUEST);
            }

            if (endDate.isBefore(startDate))
            {
                return new ResponseEntity<String>("Некорректный запрос - начальная дата оказалась позже конечной", HttpStatus.BAD_REQUEST);
            }
        }

        //Protection from ddos attacks - the bigger date range is the longer it takes for server to process query
        if (startDate.plusDays(31).isBefore(endDate))
        {
            endDate = startDate.plusDays(31);
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
                    "Срвер не смог получить информацию о возможных датах для посещения из базы данных или обработать её",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Throwable e)
        {
            return new ResponseEntity<String>("Unknown error - retry later", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(jsonCalendar, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/getDoctorShortInfo")
    public ResponseEntity<String> getDoctorsShortInfo(@RequestParam(name = "doctorIds", required = false) List<Long> doctorIds)
    {
        if (doctorIds == null)
        {
            doctorIds = scheduleService.getAllDoctorSchedules().stream().map(doctorSchedule -> doctorSchedule.getRelatedDoctor().getId()).toList();
        }

        String doctorsShortInformation = null;
        try
        {
            doctorsShortInformation = scheduleViewService.getDoctorsShortInformationJson(doctorIds);
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>(
                    "Сервер не смог получить сокращенную информацию о докторах из базы данных",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e)
        {
            return new ResponseEntity<String>("Неизвестная ошибка", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(doctorsShortInformation, HttpStatus.OK);
    }
}
