package com.NetCracker.Entities.Schedule.ScheduleElements;

import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.gson.annotations.Expose;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

import static com.NetCracker.Utils.TimeIntervalUtils.floorHalfHourInterval;

/**
 * This class represents status of a doctor in a time period (30 mins), compressed to a byte
 * 1st (the right bit of a byte) signals if a doctor is working or not
 * 2nd signals if a doctor already has an assignment in this period of time
 * You may add more states
 */

@Entity
@Table(name = "schedule_interval")
@IdClass(ScheduleIntervalId.class)
public class ScheduleInterval {
    @Id
    @ManyToOne
    @JoinColumn(name = "doctor_schedule", referencedColumnName = "DOCTOR_SCHEDULE_ID")
    @Expose(serialize = false, deserialize = false)
    @JsonBackReference
    private DoctorSchedule doctorSchedule;

    //This field stores the 30-minute interval that field state refers to.
    @NotNull
    @Column(name = "interval_start_time")
    @Id
    @Expose
    protected LocalDateTime intervalStartTime;

    @Column(name = "is_assigned")
    @Expose
    private boolean isAssigned;

    /**
     * For Hibernate only
     */
//    @Deprecated
//    ScheduleInterval()
//    {}
    public ScheduleInterval() {
    }

    /**
     * Consider using other constructor if you want to persist (save to DB) created variable.
     */
    public ScheduleInterval(LocalDateTime intervalStartTime) {
        this.intervalStartTime = floorHalfHourInterval(intervalStartTime);
    }

    public ScheduleInterval(DoctorSchedule schedule, LocalDateTime intervalStartTime) throws NullPointerException {
        this(intervalStartTime);
        doctorSchedule = schedule;
        isAssigned = false;
    }

    /**
     * @param schedule   describes schedule that this interval is associated with
     * @param dateTime   are unique values for each doctor. Seconds and nanoseconds are ignored (set to 0)
     * @param isAssigned defines if doctor has an appointment
     */
    public ScheduleInterval(DoctorSchedule schedule, LocalDateTime dateTime, boolean isAssigned) {
        this(schedule, dateTime);
        this.isAssigned = isAssigned;
    }

    public ScheduleInterval(DoctorSchedule schedule, LocalDate dayToApplyPatternFrom, SchedulePatternInterval patternInterval) {
        this(schedule, dayToApplyPatternFrom.atStartOfDay().plus(
                Duration.between(LocalDate.EPOCH.atStartOfDay(), patternInterval.getIntervalStartTime())));
        this.isAssigned = false;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public DoctorSchedule getDoctorSchedule() {
        return doctorSchedule;
    }

    public LocalDateTime getIntervalStartTime() {
        return intervalStartTime;
    }

    public void setDoctorSchedule(DoctorSchedule doctorSchedule) {
        this.doctorSchedule = doctorSchedule;
    }

    public void setAssigned(boolean isAssigned) {
        this.isAssigned = isAssigned;
    }

    public void setIntervalStartTime(LocalDateTime intervalStartTime) {
        this.intervalStartTime = intervalStartTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleInterval)) return false;
        ScheduleInterval that = (ScheduleInterval) o;
        return intervalStartTime.equals(that.intervalStartTime) && doctorSchedule.getId().equals(that.doctorSchedule.getId()) && isAssigned == that.isAssigned;
    }

    @Override
    public int hashCode() {
        return Objects.hash(intervalStartTime, (doctorSchedule == null) ? null : doctorSchedule.getId(), isAssigned);
    }

    public static class ScheduleIntervalDateAscendComparator implements Comparator<ScheduleInterval> {
        @Override
        public int compare(ScheduleInterval o1, ScheduleInterval o2) {
            return o1.getIntervalStartTime().compareTo(o2.getIntervalStartTime());
        }
    }

    public static final Comparator<ScheduleInterval> dateAscendComparator = new Comparator<ScheduleInterval>() {
        @Override
        public int compare(ScheduleInterval o1, ScheduleInterval o2) {
            return o1.getIntervalStartTime().compareTo(o2.getIntervalStartTime());
        }
    };
}
