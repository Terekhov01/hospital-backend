package com.NetCracker.Entities.Schedule;

import com.NetCracker.Entities.Schedule.ScheduleElements.SchedulePatternInterval;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.TreeSet;

/**
 * This entity represents doctor's working time.
 * We assume that each 2 weeks doctors have a repeated schedule so only 2-week data is stored.
 * 2-week interval is divided by 30-minute intervals. Doctor may either work or not during this small period.
 */
@Entity
@Table(name = "schedule_pattern")
public class SchedulePattern
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    private Long id;

    private String name;

    @NotNull
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "schedulePattern")
    //@JoinColumn(name = "schedule_pattern_id", referencedColumnName = "id", nullable = false)
    private Set<SchedulePatternInterval> stateSet;

    public SchedulePattern()
    {
        //There are 672 30-minute intervals in 2 weeks
        stateSet = new TreeSet<>(SchedulePatternInterval.dateAscendComparator);
    }

    public SchedulePattern(Set<SchedulePatternInterval> stateSet)
    {
        this.stateSet = stateSet;
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public Set<SchedulePatternInterval> getStateSet()
    {
        return stateSet;
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

    public void setStateSet(TreeSet<SchedulePatternInterval> stateSet)
    {
        this.stateSet = stateSet;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setStateSet(Set<SchedulePatternInterval> stateSet)
    {
        this.stateSet = stateSet;
    }
}
