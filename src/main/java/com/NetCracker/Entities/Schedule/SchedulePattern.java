package com.NetCracker.Entities.Schedule;

import com.NetCracker.Entities.Schedule.ScheduleElements.ScheduleInterval;
import com.NetCracker.Entities.Schedule.ScheduleElements.SchedulePatternInterval;
import org.hibernate.annotations.SortComparator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This entity represents doctor's working time.
 * All the time is divided by 30-minute intervals. Doctor may either work or not during this small period.
 */
@Entity
@Table(name = "schedule_pattern")
public class SchedulePattern {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SCHEDULE_PATTERN_ID", unique = true, nullable = false, updatable = false)
    @NotNull
    private Long id;

    @NotNull
    @Column(name = "NAME")
    private String name;

    @NotNull
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "schedulePattern")
    @SortComparator(SchedulePatternInterval.SchedulePatternIntervalDateAscendComparator.class)
    private SortedSet<SchedulePatternInterval> stateSet;

    public SchedulePattern() {
    }

    public SchedulePattern(String name) {
        this.name = name;
        stateSet = new TreeSet<SchedulePatternInterval>(SchedulePatternInterval.dateAscendComparator);
    }

    public SchedulePattern(String name, NavigableSet<SchedulePatternInterval> stateSet) {
        this.name = name;
        this.stateSet = stateSet;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SortedSet<SchedulePatternInterval> getStateSet() {

//        if (!(stateSet instanceof TreeSet<SchedulePatternInterval>))
//        {
//            throw new IllegalStateException("stateSet does not contain a TreeSet!");
//        }

        if (!(stateSet instanceof TreeSet)) {
            throw new IllegalStateException("stateSet does not contain a TreeSet!");
        } else {
            TreeSet treeSet = (TreeSet) stateSet;
            for (Object e : treeSet) {
                if (!(e instanceof SchedulePatternInterval)) {
                    throw new IllegalStateException("stateSet does not contain a TreeSet!");
                }
            }
        }

        return stateSet;
    }

    /**
     * This method is to be used by Hibernate only
     *
     * @param id is not updated in database
     */
    @Deprecated
    public void setId(Long id) {
        this.id = id;
    }

    public void setStateSet(SortedSet<SchedulePatternInterval> stateSet) {
        this.stateSet = stateSet;
    }

    public void setName(String name) {
        this.name = name;
    }
}
