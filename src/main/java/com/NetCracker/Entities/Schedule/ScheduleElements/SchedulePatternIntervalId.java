package com.NetCracker.Entities.Schedule.ScheduleElements;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


public class SchedulePatternIntervalId implements Serializable {
    private Long schedulePattern;

    private Integer dayNumber;

    private LocalDateTime intervalStartTime;

    public SchedulePatternIntervalId() {
    }

    public SchedulePatternIntervalId(Long schedulePattern, Integer dayNumber, LocalDateTime intervalStartTime) {
        this.schedulePattern = schedulePattern;
        this.dayNumber = dayNumber;
        this.intervalStartTime = intervalStartTime;
    }

    public Long getSchedulePattern() {
        return schedulePattern;
    }

    public Integer getDayNumber() {
        return dayNumber;
    }

    public LocalDateTime getIntervalStartTime() {
        return intervalStartTime;
    }

    public void setSchedulePattern(Long schedulePattern) {
        this.schedulePattern = schedulePattern;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public void setIntervalStartTime(LocalDateTime intervalStartTime) {
        this.intervalStartTime = intervalStartTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchedulePatternIntervalId)) return false;
        SchedulePatternIntervalId that = (SchedulePatternIntervalId) o;
        return Objects.equals(schedulePattern, that.schedulePattern) && Objects.equals(intervalStartTime, that.intervalStartTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schedulePattern, intervalStartTime);
    }
}
