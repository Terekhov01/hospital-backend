package com.NetCracker.entities.schedule.scheduleElements;

import com.NetCracker.entities.schedule.SchedulePattern;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Objects;

@Entity
@Table(name = "schedule_pattern_interval")
@IdClass(SchedulePatternIntervalId.class)
public class SchedulePatternInterval
{
    @Id
    @ManyToOne
    @JoinColumn(name = "schedule_pattern_id", referencedColumnName = "id")
    private SchedulePattern schedulePattern;

    @NotNull
    @Column(name = "day_number")
    @Id
    private Integer dayNumber;

    //This field stores the 30-minute interval that field state refers to.
    @NotNull
    @Column(name = "interval_start_time")
    @Id
    private LocalTime intervalStartTime;

    /**
     * This constructor is for Hibernate only. Don't use it!
     */
    @Deprecated
    public SchedulePatternInterval()
    {
    }

    /**
     * Consider using other constructor if you want to persist (save to DB) a variable
     */
    public SchedulePatternInterval(Integer dayNumber, LocalTime intervalStartTime)
    {
        this.dayNumber = dayNumber;
        this.intervalStartTime = intervalStartTime.withSecond(0);
        this.intervalStartTime = this.intervalStartTime.withNano(0);
    }

    public SchedulePatternInterval(Integer dayNumber, LocalTime intervalStartTime, SchedulePattern pattern)
    {
        this(dayNumber, intervalStartTime);
        this.schedulePattern = pattern;
    }

    public SchedulePattern getSchedulePattern()
    {
        return schedulePattern;
    }

    public Integer getDayNumber()
    {
        return dayNumber;
    }

    public LocalTime getIntervalStartTime()
    {
        return intervalStartTime;
    }

    public void setSchedulePattern(SchedulePattern schedulePattern)
    {
        this.schedulePattern = schedulePattern;
    }

    public void setDayNumber(Integer dayNumber)
    {
        this.dayNumber = dayNumber;
    }

    public void setIntervalStartTime(LocalTime intervalStartTime)
    {
        this.intervalStartTime = intervalStartTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof SchedulePatternInterval)) return false;
        SchedulePatternInterval that = (SchedulePatternInterval) o;
        return intervalStartTime.equals(that.getIntervalStartTime()) && schedulePattern.getId() == that.schedulePattern.getId();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(intervalStartTime, (schedulePattern == null) ? null : schedulePattern.getId());
    }

    public static class SchedulePatternIntervalDateAscendComparator implements Comparator<SchedulePatternInterval>
    {
        @Override
        public int compare(SchedulePatternInterval o1, SchedulePatternInterval o2)
        {
            int dayComparison = o1.dayNumber.compareTo(o2.dayNumber);
            if (o1.dayNumber.compareTo(o2.dayNumber) == 0)
            {
                return o1.getIntervalStartTime().compareTo(o2.getIntervalStartTime());
            }

            return dayComparison;
        }
    }

    public static final Comparator<SchedulePatternInterval> dateAscendComparator = new Comparator<SchedulePatternInterval>() {
        @Override
        public int compare(SchedulePatternInterval o1, SchedulePatternInterval o2) {
            int dayComparison = o1.dayNumber.compareTo(o2.dayNumber);
            if (o1.dayNumber.compareTo(o2.dayNumber) == 0)
            {
                return o1.getIntervalStartTime().compareTo(o2.getIntervalStartTime());
            }

            return dayComparison;
        }
    };
}
