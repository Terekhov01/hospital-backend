package com.NetCracker.Services;

import com.NetCracker.Entities.DoctorSchedule;
import com.NetCracker.Entities.DoctorSchedulePattern;
import com.NetCracker.Entities.Schedule;
import com.NetCracker.Entities.ScheduleStatus;
import com.NetCracker.Repositories.DoctorScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleMaintenanceService
{
    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    public static DoctorSchedulePattern createCommonWorkingPattern()
    {
        List<ScheduleStatus> scheduleStatusList = new ArrayList<>();

        for (int i = 0; i < 672; i++)
        {
            ScheduleStatus status = new ScheduleStatus();
            if (i % 48 > 18 && i % 48 < 34)
            {
                status.setWorking(true);
            }
            scheduleStatusList.add(status);
        }

        return new DoctorSchedulePattern(scheduleStatusList);
    }

    public static void prolongScheduleByWorkingPattern(DoctorSchedule schedule, DoctorSchedulePattern pattern)
    {
        pattern.getStatusList().forEach(status ->
        {
            schedule.getIntervalStatusList().add(status);
        });
    }

    /**
     * This function allows you to get information about any time in doctor's schedule.
     * It calculates offsets and presents you a status.
     * @param time - time in doctor's schedule you are interested in
     * @return status of a doctor if schedule contains required date, or else null
     */
    public static ScheduleStatus getStatus(DoctorSchedule schedule, LocalDateTime time)
    {
        if (schedule.getScheduleStartDate().isAfter(time))
        {
            return null;
        }

        Duration difference = Duration.between(schedule.getScheduleStartDate(), time);

        long hoursDifference = difference.toHours();
        long minutesAdditive = difference.toMinutes() % 60;

        // Possible overflow is checked 6 strings below
        long offsetInList = hoursDifference * 2;

        if (minutesAdditive >= 15 && minutesAdditive < 45)
        {
            offsetInList++;
        }

        if (hoursDifference > (Integer.MAX_VALUE / 2 - 1) || offsetInList > schedule.getIntervalStatusList().size())
        {
            //param time point to the time that is after the last scheduled date
            return null;
        }

        return schedule.getIntervalStatusList().get((int) offsetInList);
    }

    @Transactional
    public void forcePersistSchedule(Schedule schedule)
    {
        doctorScheduleRepository.saveAll(schedule.getAllDoctorSchedules());
    }

    @Override
    public String toString()
    {
        return "ScheduleMaintenanceService{" +
                "doctorScheduleRepository=" + doctorScheduleRepository +
                '}';
    }
}
