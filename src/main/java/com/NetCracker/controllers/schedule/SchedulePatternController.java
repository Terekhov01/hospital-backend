package com.NetCracker.controllers.schedule;

import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.schedule.SchedulePattern;
import com.NetCracker.services.security.AuthenticationService;
import com.NetCracker.services.doctor.DoctorUserService;
import com.NetCracker.services.schedule.SchedulePatternService;
import com.NetCracker.services.schedule.SchedulePatternViewService;
import com.NetCracker.services.schedule.ScheduleService;
import com.NetCracker.utils.StringUtils;
import com.google.gson.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

//All controllers can be optimized. The easiest way is to reduce throw/catch sections amount
@CrossOrigin(origins = "*", maxAge = 3600)
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

    @Autowired
    private AuthenticationService authenticationService;

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/list-patterns")
    public ResponseEntity<String> getSchedulePatternList(Authentication authentication)
    {
        String schedulePatternList;

        Doctor authenticatedDoctor = null;
        try
        {
            authenticatedDoctor = authenticationService.getAuthenticatedDoctor(authentication);
        }
        catch (ClassCastException e)
        {
            return new ResponseEntity<String>("???????????????????????????? ???? ????????????????", HttpStatus.UNAUTHORIZED);
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("???????????????????????????? ???? ????????????????. ???????????? ?????????????? ?????????????????? ?? ????",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (authenticatedDoctor == null)
        {
            return new ResponseEntity<String>("???? ???????????? ???????????? ?? ?????????? ?????????????????????????????? ??????????????????????. " +
                    "???????????? ??????????????", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try
        {
            schedulePatternList = schedulePatternViewService.getSchedulePatternList(authenticatedDoctor.getId());
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("???? ???????????????????? ?????????????????? ?????????? ???????????????? ???? ???????? ????????????",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<String>(schedulePatternList, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/view-pattern")
    public ResponseEntity<String> viewSchedulePattern(@RequestParam(name = "patternName", required = true) String patternName,
                                                      Authentication authentication)
    {
        Doctor authenticatedDoctor = null;
        try
        {
            authenticatedDoctor = authenticationService.getAuthenticatedDoctor(authentication);
        }
        catch (ClassCastException e)
        {
            return new ResponseEntity<String>("???????????????????????????? ???? ????????????????.", HttpStatus.UNAUTHORIZED);
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("???????????????????????????? ???? ????????????????. ???????????? ?????????????? ?????????????????? ?? ????.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (authenticatedDoctor == null)
        {
            return new ResponseEntity<String>("???? ???????????? ???????????? ?? ?????????? ?????????????????????????????? ??????????????????????. " +
                    "???????????? ???? ??????????????", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String patternJson = null;

        try
        {
            patternJson = schedulePatternViewService.toJson(patternName, authenticatedDoctor.getId());
        }
        catch (AccessDeniedException e)
        {
            return new ResponseEntity<String>("???????????? ???? ????????????. ??????????????????, ?????? ???? " +
                    "????????????????????", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(patternJson, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PostMapping("/add-pattern")
    public ResponseEntity<String> addSchedulePattern(@RequestBody Map<String, Object> responseBody,
                                                     Authentication authentication)
    {
        Doctor authenticatedDoctor = null;
        try
        {
            authenticatedDoctor = authenticationService.getAuthenticatedDoctor(authentication);
        }
        catch (ClassCastException e)
        {
            return new ResponseEntity<String>("???????????????????????????? ???? ????????????????", HttpStatus.UNAUTHORIZED);
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("???????????????????????????? ???? ????????????????. ???????????? ?????????????? ?????????????????????? ?? " +
                    "???????? ????????????", HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (authenticatedDoctor == null)
        {
            return new ResponseEntity<String>("???? ???????????? ???????????? ?? ?????????? ?????????????????????????????? ??????????????????????. " +
                    "???????????? ???? ??????????????", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<Object> updates;
        try
        {
            var bodyParameters = (Map<String, Object>) responseBody.get("params");
            updates = (List<Object>) bodyParameters.get("updates");
        }
        catch (ClassCastException e)
        {
            return new ResponseEntity<String>("???????? ?????????????? ???????????????? ???????????????????????? ????????????????????",
                    HttpStatus.BAD_REQUEST);
        }

        if (updates.size() != 1)
        {
            return new ResponseEntity<String>("?????????????? ?????????? ???????????????????? ?? ??????????????",
                    HttpStatus.BAD_REQUEST);
        }

        Map<String, String> parameter;
        try
        {
            parameter = (Map<String, String>) updates.get(0);
        }
        catch (ClassCastException e)
        {
            return new ResponseEntity<String>("???? ???????????????????? ???????????????????? ???????? ??????????????", HttpStatus.BAD_REQUEST);
        }

        if (!parameter.get("param").equals("schedulePattern"))
        {
            return new ResponseEntity<String>("???? ???????????????????? ???????????????????? ???????? ??????????????", HttpStatus.BAD_REQUEST);
        }
        String schedulePattern = parameter.get("value");

        SchedulePattern newSchedulePattern;
        try
        {
            newSchedulePattern = schedulePatternViewService.fromJson(schedulePattern, authenticatedDoctor.getId());
        }
        catch (InvalidParameterException e)
        {
            return new ResponseEntity<String>("?????? ?????????????? ???? ?????????? ???????? ???????????? ??????????????", HttpStatus.BAD_REQUEST);
        }
        catch(ChangeSetPersister.NotFoundException e)
        {
            return new ResponseEntity<String>("???? ?????????????? ?????????? ?????????????????????????????? ??????????????",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (JsonParseException e)
        {
            return new ResponseEntity<String>("???? ???????????????????? ???????????????????? ???????? ??????????????. ???? ?????????????? ?????????????? ????????????",
                    HttpStatus.BAD_REQUEST);
        }

        try
        {
            schedulePatternService.save(newSchedulePattern);
        }
        catch(DataAccessException e)
        {
            return new ResponseEntity<String>("???? ?????????????? ?????????????????? ????????????. ???????????? ?????? ????????????????????????????" +
                    " ?? ?????????? ????????????. ????????????????, ???????????? ?? ?????????? ?????????????????? ?????? ????????????????????", HttpStatus.SERVICE_UNAVAILABLE);
        }
        catch(Exception e)
        {
            return new ResponseEntity<String>("?????????????????????? ????????????", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PatchMapping("/apply-pattern")
    public ResponseEntity<String> prolongSchedule(@RequestBody Map<String, Object> requestBody, Authentication authentication)
    {
        Doctor authenticatedDoctor = null;
        try
        {
            authenticatedDoctor = authenticationService.getAuthenticatedDoctor(authentication);
        }
        catch (ClassCastException e)
        {
            return new ResponseEntity<String>("???????????????????????????? ???? ????????????????", HttpStatus.UNAUTHORIZED);
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("???????????????????????????? ???? ????????????????. ???????????? ?????????????? ?????????????????? ?? ????",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (authenticatedDoctor == null)
        {
            return new ResponseEntity<String>("???? ???????????? ???????????? ?? ?????????? ?????????????????????????????? ??????????????????????. " +
                    "???????????? ??????????????", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String patternName;
        String dateToApplyStr;
        Integer repeatCnt;
        try
        {
            patternName = (String) requestBody.get("patternName");
            dateToApplyStr = (String) requestBody.get("dateToApplyStr");
            repeatCnt = (Integer) requestBody.get("repeatCnt");
        }
        catch (ClassCastException | NullPointerException e)
        {
            return new ResponseEntity<String>("???????????????????????? ????????????. ???? ?????????????? ???????????????????? ??????????????????.",
                    HttpStatus.BAD_REQUEST);
        }

        LocalDateTime dateToApply = StringUtils.stringToDateTime(dateToApplyStr);
        if (dateToApply == null)
        {
            return new ResponseEntity<String>("???????????????????????? ???????????? - ???? ???????????????????? ???????????????????? ???????? ???????????????????? ??????????????.",
                    HttpStatus.BAD_REQUEST);
        }

        SchedulePattern requestedPattern;
        try
        {
            requestedPattern = schedulePatternService.findPatternByNameAndRelatedDoctor(patternName,
                    authenticatedDoctor.getId());
        }
        catch(DataAccessException e)
        {
            return new ResponseEntity<String>("???????????? ???? ????????????. ???????????? ???????? ?? ?????????? ????????????",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (requestedPattern == null)
        {
            return new ResponseEntity<String>("???????????? ?? ?????????????????? ???????????????????? ????????????????????????", HttpStatus.NOT_FOUND);
        }

        try
        {
            scheduleService.applyPatternToSchedule(authenticatedDoctor, requestedPattern, dateToApply.toLocalDate(), repeatCnt);
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("???? ???????????????? ???????????????? ????????????????????. ???????????? ?????????? ?? ?????????? ????????????",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deletePattern(@RequestParam(name = "patternName") String patternName, Authentication authentication)
    {
        Doctor authenticatedDoctor = null;
        try
        {
            authenticatedDoctor = authenticationService.getAuthenticatedDoctor(authentication);
        }
        catch (ClassCastException e)
        {
            return new ResponseEntity<String>("???????????????????????????? ???? ????????????????", HttpStatus.UNAUTHORIZED);
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("???????????????????????????? ???? ????????????????. ???????????? ?????????????? ?????????????????? ?? ????",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (authenticatedDoctor == null)
        {
            return new ResponseEntity<String>("???? ???????????? ???????????? ?? ?????????? ?????????????????????????????? ??????????????????????. " +
                    "???????????? ??????????????", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SchedulePattern requestedPattern;
        try
        {
            requestedPattern = schedulePatternService.findPatternByNameAndRelatedDoctor(patternName,
                    authenticatedDoctor.getId());
        }
        catch(DataAccessException e)
        {
            return new ResponseEntity<String>("???????????? ?? ?????????????????? ???????????????????? ???????????????????????? ?????? ????????????????????",
                    HttpStatus.BAD_REQUEST);
        }

        try
        {
            schedulePatternService.delete(requestedPattern);
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("???????????????????? ?????????????? ????????????. ???????? ???????????? ????????????????????",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<String>("", HttpStatus.OK);
    }
}
