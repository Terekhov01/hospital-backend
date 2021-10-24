package com.NetCracker.Controllers;

import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;
import com.NetCracker.Services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import static java.util.Map.entry;
import java.util.stream.Collectors;

@RestController
public class ScheduleController
{
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/schedule")
    public void presentAllSchedules(@RequestParam(name = "doctorIds", required = false) Long[] doctorIds,
                                    @RequestParam(name = "startDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
                                    @RequestParam(name = "endDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
                                    @RequestParam(name = "getFreeTimeOnly", required = false) boolean getFreeTimeOnly)
    {
        Set<DoctorSchedule> schedules = scheduleService.getDoctorSchedule(Arrays.stream(doctorIds).toList());

        ScheduleInterval intervalStart = new ScheduleInterval(startDateTime);
        ScheduleInterval intervalEnd = new ScheduleInterval(endDateTime);

        //Required data - each doctor is matched with set of scheduleIntervals - time, when (s)he is working (not busy)
        Map<Long, SortedSet<ScheduleInterval>> scheduleRangedIntervals = schedules.stream().collect(
                Collectors.toMap(
                    schedule -> schedule.getName(),
                    schedule -> schedule.getStateSet().subSet(intervalStart, intervalEnd)
                )
            );

        if (getFreeTimeOnly)
        {
            //Remove all assigned intervals
            for (var intervalSet : scheduleRangedIntervals.values())
            {
                intervalSet.removeIf(ScheduleInterval::isAssigned);
            }

            //Remove all entries that have empty value
            scheduleRangedIntervals = scheduleRangedIntervals.entrySet().stream().filter(pair -> pair.getValue().size() != 0)
                    .map(pair -> entry(pair.getKey(), pair.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }


        //TODO - return HttpResponse, not a map
        return scheduleRangedIntervals;
    }
}
