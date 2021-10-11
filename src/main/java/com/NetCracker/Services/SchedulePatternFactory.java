package com.NetCracker.Services;

import com.NetCracker.Entities.SchedulePattern;
import com.NetCracker.Entities.ScheduleState;

import java.util.ArrayList;
import java.util.List;

public class SchedulePatternFactory
{
    /**
     *
     * @param workingDayStart an hour when employee starts to work, e.g.
     * @param workingDayEnd an hour
     * @return
     */
    public static SchedulePattern createCommonWorkingPattern(int workingDayStart, int workingDayEnd)
    {
        List<ScheduleState> scheduleStateList = new ArrayList<>();

        for (int i = 0; i < 672; i++)
        {
            ScheduleState status = new ScheduleState();
            if (i % 48 > workingDayStart * 2 && i % 48 < workingDayEnd * 2)
            {
                status.setWorking(true);
            }
            scheduleStateList.add(status);
        }

        return new SchedulePattern(scheduleStateList);
    }


}
