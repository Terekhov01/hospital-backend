package com.NetCracker.Entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * This entity represents doctor's working time.
 * We assume that each 2 weeks doctors have a repeated schedule so only 2-week data is stored.
 * 2-week interval is divided by 30-minute intervals. Doctor may either work or not during this small period.
 */
@Entity
public class DoctorWorkingPattern
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    @NotNull
    private Long id;

    @NotNull
    @ElementCollection
    private List<ScheduleStatus> statusList;

    public DoctorWorkingPattern()
    {
        //There are 672 30-minute intervals in 2 weeks
        statusList = new ArrayList<>(672);
    }

    public DoctorWorkingPattern(List<ScheduleStatus> statusList)
    {
        this.statusList = statusList;
    }

    public Long getId()
    {
        return id;
    }

    public List<ScheduleStatus> getStatusList()
    {
        return statusList;
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

    public void setStatusList(List<ScheduleStatus> statusList)
    {
        this.statusList = statusList;
    }
}
