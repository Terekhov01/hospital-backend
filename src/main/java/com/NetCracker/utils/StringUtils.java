package com.NetCracker.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class StringUtils
{
    public static List<Long> stringToLongList(String str) throws NumberFormatException
    {
        try
        {
            return Arrays.stream(str.split(",")).map(Long::valueOf).collect(Collectors.toList());
        }
        catch(NumberFormatException e)
        {
            return null;
        }
    }

    private static String prepareDateStringToParse(String dateStr)
    {
        if (dateStr.charAt(0) == ' ')
        {
            StringBuilder builder = new StringBuilder(dateStr);
            builder.setCharAt(0, '+');
            return builder.toString();
        }
        return dateStr;
    }

    public static LocalDateTime stringToDateTime(String str)
    {
        DateTimeFormatter ISOFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        try
        {
            return ZonedDateTime.parse(prepareDateStringToParse(str), ISOFormatter).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        }
        catch (DateTimeParseException e)
        {
            return null;
        }
    }

    /**
     * Input - 2021-12-27T21:00:00.000Z, output - 2021-12-27T21:00 (if system time is Moscow)
     * @param str - ISO representation of date
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String str)
    {
        DateTimeFormatter ISOFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        try
        {
            return LocalDateTime.parse(prepareDateStringToParse(str), ISOFormatter);
        }
        catch (DateTimeParseException e)
        {
            return null;
        }
    }
}
