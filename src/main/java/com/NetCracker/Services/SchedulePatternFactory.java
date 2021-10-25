package com.NetCracker.Services;

import com.NetCracker.Entities.Schedule.ScheduleElements.SchedulePatternInterval;
import com.NetCracker.Entities.Schedule.SchedulePattern;
import com.NetCracker.Utils.TimeIntervalUtils;

import java.time.*;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
        SortedSet<SchedulePatternInterval> scheduleIntervalSet = new TreeSet<>(SchedulePatternInterval.dateAscendComparator);

        SchedulePattern pattern = new SchedulePattern(name);

        for (int i = 0; i < daysAmount; i++)
        {
            //Pattern's base point is an epoch's midnight
            LocalDateTime counterDateTime = LocalDateTime.of(LocalDate.EPOCH.plusDays(i), workingDayStart);
            for (; counterDateTime.toLocalTime().compareTo(workingDayEnd) < 0; counterDateTime = counterDateTime.plusMinutes(30))
            {
                scheduleIntervalSet.add(new SchedulePatternInterval(counterDateTime, pattern));
            }
        }

        pattern.setStateSet(scheduleIntervalSet);

        return pattern;
    }
}