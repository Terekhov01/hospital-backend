package com.NetCracker.Services;

import com.NetCracker.Entities.Schedule.ScheduleElements.SchedulePatternInterval;
import com.NetCracker.Entities.Schedule.SchedulePattern;
import com.NetCracker.Utils.TimeIntervalUtils;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
public class SchedulePatternFactory
{
    /**
     *
     * @param workingDayStart an hour when employee starts to work every day (always the same)
     * @param workingDayEnd an hour
     * @return schedule pattern that has
     */
    public static SchedulePattern createCommonWorkingPattern(String name, int daysAmount, LocalTime workingDayStart, LocalTime workingDayEnd)
    {
        workingDayStart = TimeIntervalUtils.floorHalfHourInterval(workingDayStart);
        workingDayEnd = TimeIntervalUtils.ceilHalfHourInterval(workingDayEnd);
        NavigableSet<SchedulePatternInterval> scheduleIntervalSet = new TreeSet<>(SchedulePatternInterval.dateAscendComparator);

        SchedulePattern pattern = new SchedulePattern(name);

        for (int i = 0; i < daysAmount; i++)
        {
            //Pattern's base point is an epoch's midnight
            LocalTime counterTime = workingDayStart;
            for (; counterTime.compareTo(workingDayEnd) < 0; counterTime = counterTime.plusMinutes(30))
            {
                scheduleIntervalSet.add(new SchedulePatternInterval(i, counterTime, pattern));
            }
        }

        pattern.setStateSet(scheduleIntervalSet);

        return pattern;
    }
}