package com.NetCracker.entities.schedule;

import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.schedule.scheduleElements.SchedulePatternInterval;
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
@Table(name = "schedule_pattern", uniqueConstraints={@UniqueConstraint(columnNames = {"name" , "doctor_id"})})
public class SchedulePattern
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    @NotNull
    private Long id;

    @NotNull
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

    public SchedulePattern(String name) {
        this.name = name;
        stateSet = new TreeSet<SchedulePatternInterval>(SchedulePatternInterval.dateAscendComparator);
    }

    public SchedulePattern(String name, NavigableSet<SchedulePatternInterval> stateSet) {
        this.name = name;
        this.stateSet = stateSet;
    }

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

    public Doctor getRelatedDoctor()
    {
        return relatedDoctor;
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

    public void setRelatedDoctor(Doctor relatedDoctor)
    {
        this.relatedDoctor = relatedDoctor;
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
