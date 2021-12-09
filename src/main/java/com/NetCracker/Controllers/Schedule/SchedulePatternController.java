package com.NetCracker.Controllers.Schedule;

import com.NetCracker.Entities.Doctor.Doctor;
import com.NetCracker.Entities.Schedule.SchedulePattern;
import com.NetCracker.Services.Doctor.DoctorUserService;
import com.NetCracker.Services.Schedule.SchedulePatternService;
import com.NetCracker.Services.Schedule.SchedulePatternViewService;
import com.NetCracker.Services.Schedule.ScheduleService;
import com.NetCracker.Services.UserDetailsImpl;
import com.NetCracker.Utils.StringUtils;
import com.google.gson.JsonParseException;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("schedule-pattern")
@RestController
public class SchedulePatternController
{
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private SchedulePatternService schedulePatternService;

    @Autowired
    private SchedulePatternViewService schedulePatternViewService;

    @Autowired
    private DoctorUserService doctorUserService;

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/list-patterns")
    public ResponseEntity<String> getSchedulePatternList(Authentication authentication)
    {
        String schedulePatternList;
        try
        {
            schedulePatternList = schedulePatternViewService.getSchedulePatternList(((UserDetailsImpl)authentication.getPrincipal()).getId());
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("Could not extract pattern names from database!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(schedulePatternList, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PostMapping("/add-pattern")
    public ResponseEntity<String> addSchedulePattern(@RequestBody Map<String, Object> responseBody, Principal principal)
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
            newSchedulePattern = schedulePatternService.fromJson(schedulePattern, principal.getName());
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

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PatchMapping("/apply-pattern")
    public ResponseEntity<String> prolongSchedule(@RequestBody Map<String, Object> requestBody, Principal principal)
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

        LocalDateTime dateToApply = StringUtils.stringToDateTime(dateToApplyStr);
        if (dateToApply == null)
        {
            return new ResponseEntity<String>("Invalid request - dateToApply - could not parse temporal value", HttpStatus.BAD_REQUEST);
        }

        //TODO - get current doctor - finish when spring security done
        Doctor doctor = doctorUserService.findById(1L);
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
}
