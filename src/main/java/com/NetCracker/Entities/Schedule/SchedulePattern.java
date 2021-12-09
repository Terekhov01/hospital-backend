package com.NetCracker.Entities.Schedule;

import com.NetCracker.Entities.Doctor.Doctor;
import com.NetCracker.Entities.Schedule.ScheduleElements.SchedulePatternInterval;
import com.google.gson.annotations.Expose;
import org.hibernate.annotations.SortComparator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This entity represents doctor's working time.
 * All the time is divided by 30-minute intervals. Doctor may either work or not during this small period.
 */
@Entity
@Table(name = "schedule_pattern")
public class SchedulePattern
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    @NotNull
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    @NotNull
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "schedulePattern")
    @SortComparator(SchedulePatternInterval.SchedulePatternIntervalDateAscendComparator.class)
    private SortedSet<SchedulePatternInterval> stateSet;

    @NotNull
    private Integer daysLength;

    @NotNull
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    @Expose(serialize = false, deserialize = false)
    private Doctor relatedDoctor;

    /**
     * For Hibernate only
     */
    @Deprecated
    public SchedulePattern()
    {}

    public SchedulePattern(Doctor relatedDoctor, String name)
    {
        this.relatedDoctor = relatedDoctor;
        this.name = name;
        this.daysLength = 0;
        stateSet = new TreeSet<SchedulePatternInterval>(SchedulePatternInterval.dateAscendComparator);
    }

    public SchedulePattern(Doctor relatedDoctor, String name, Integer daysLength, NavigableSet<SchedulePatternInterval> stateSet)
    {
        this.relatedDoctor = relatedDoctor;
        this.name = name;
        this.stateSet = stateSet;
        this.daysLength = daysLength;
        for (var state : this.stateSet)
        {
            state.setSchedulePattern(this);
        }
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public SortedSet<SchedulePatternInterval> getStateSet()
    {
        return stateSet;
    }

    public Integer getDaysLength()
    {
        return daysLength;
    }

    /**
     * This method is to be used by Hibernate only
     * @param id is not updated in database
     */
    @Deprecated
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * This method is to be used by Hibernate only
     */
    public void setStateSet(SortedSet<SchedulePatternInterval> stateSet)
    {
        this.stateSet = stateSet;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * This method is to be used by Hibernate only
     */
    @Deprecated
    public void setDaysLength(Integer daysLength)
    {
        this.daysLength = daysLength;
    }
}
