package com.NetCracker.Entities;

import javax.persistence.Embeddable;
import java.util.Objects;

/**
 * This class represents status of a doctor in a time period (30 mins), compressed to a byte
 * 1st (the right bit of a byte) signals if a doctor is working or not
 * 2nd signals if a doctor already has an assignment in this period of time
 * You may add more states
 */

@Embeddable
public class ScheduleStatus
{
    byte status;

    public ScheduleStatus()
    {
        status = 0;
    }

    public ScheduleStatus(boolean isWorking, boolean isBusy)
    {
        status = 0;
        setWorking(isWorking);
        setBusy(isBusy);
    }

    public byte getStatus()
    {
        return status;
    }

    public boolean getIsWorking()
    {
        return (status & 1) == 1;
    }

    public boolean getIsBusy()
    {
        return (status & (1 << 1)) == (1 << 1);
    }

    public void setStatus(byte status)
    {
        this.status = status;
    }

    public void setWorking(boolean value)
    {
        if (value)
        {
            status = (byte) (status | 1);
            return;
        }

        //11111110 bin = 126 dec
        status = (byte) (status & (Byte.MAX_VALUE - 1));
    }

    public void setBusy(boolean value)
    {
        if (!value)
        {
            //11111101 bin = 125 dec
            status = (byte) (status & (Byte.MAX_VALUE - (1 << 1)));
            return;
        }

        if (getIsWorking())
        {
            status = (byte) (status | (1 << 1));
        }
        else
        {
            System.err.println("Setting a doctor busy during non-working time");
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ScheduleStatus)) return false;
        ScheduleStatus that = (ScheduleStatus) o;
        return status == that.status;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(status);
    }
}
