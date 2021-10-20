package com.NetCracker.Utils;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeIntervalUtils
{
    public static LocalDateTime floorHalfHourInterval(LocalDateTime dateTime)
    {
        LocalTime time = floorHalfHourInterval(dateTime.toLocalTime());
        return LocalDateTime.of(dateTime.toLocalDate(), time);
    }

    public static LocalDateTime ceilHalfHourInterval(LocalDateTime dateTime)
    {
        LocalTime time = ceilHalfHourInterval(dateTime.toLocalTime());
        if (time.getHour() == 0 && time.getMinute() == 0 && time.getSecond() == 0 && time.getNano() == 0)
        {
            return dateTime;
        }

        if (time.isAfter(LocalTime.of(23, 30)))
        {
            dateTime = dateTime.plusDays(1);
        }

        return LocalDateTime.of(dateTime.toLocalDate(), time);
    }

    /*public static LocalDateTime roundHalfHourInterval(LocalDateTime dateTime)
    {
        LocalTime tine = roundHalfHourInterval(dateTime.toLocalTime());
        return LocalDateTime.of(dateTime.)
    }*/

    public static LocalTime floorHalfHourInterval(LocalTime time)
    {
        if (time.getMinute() < 30)
        {
            time = time.withMinute(0);
        }
        else
        {
            time = time.withMinute(30);
        }
        time = time.withSecond(0);
        time = time.withNano(0);
        return time;
    }

    public static LocalTime ceilHalfHourInterval(LocalTime time)
    {
        if (time.getMinute() == 0 && time.getSecond() == 0 && time.getNano() == 0)
        {
            return time;
        }

        if (time.isAfter(LocalTime.of(23, 30)))
        {
            return LocalTime.of(0, 0);
        }

        if (time.getMinute() < 30 || (time.getMinute() == 30 && time.getSecond() == 0 && time.getNano() == 0))
        {
            time = time.withMinute(30);
        }
        else
        {
            time = time.withHour(time.getHour() + 1);
            time = time.withMinute(0);
        }

        time = time.withSecond(0);
        time = time.withNano(0);
        return time;
    }

    /*public static LocalTime roundHalfHourInterval()
    {

    }*/
}
