package com.NetCracker.Entities.Schedule.ScheduleElements;

import com.NetCracker.Entities.Schedule.SchedulePattern;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    @NotNull
    private SchedulePattern schedulePattern;

    //This field stores the 30-minute interval that field state refers to.
    @NotNull
    @Column(name = "interval_start_time")
    @Id
    protected LocalDateTime intervalStartTime;

    /**
     * This constructor is for Hibernate only. Don't use it!
     */
    @Deprecated
    public SchedulePatternInterval()
    {
    }

    public SchedulePatternInterval(LocalDateTime intervalStartTime)
    {
        this.intervalStartTime = intervalStartTime.withSecond(0);
        this.intervalStartTime = this.intervalStartTime.withNano(0);
    }

    public SchedulePattern getSchedulePattern()
    {
        return schedulePattern;
    }

    public LocalDateTime getIntervalStartTime()
    {
        return intervalStartTime;
    }

    public void setSchedulePattern(SchedulePattern schedulePattern)
    {
        this.schedulePattern = schedulePattern;
    }

    public void setIntervalStartTime(LocalDateTime intervalStartTime)
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

    public static final Comparator<SchedulePatternInterval> dateAscendComparator = new Comparator<SchedulePatternInterval>() {
        @Override
        public int compare(SchedulePatternInterval o1, SchedulePatternInterval o2) {
            return o1.getIntervalStartTime().compareTo(o2.getIntervalStartTime());
        }
    };
}
