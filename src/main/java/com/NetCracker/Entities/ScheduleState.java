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
public class ScheduleState
{
    private byte state;

    public ScheduleState()
    {
        state = 0;
    }

    public ScheduleState(boolean isWorking, boolean isBusy)
    {
        state = 0;
        setWorking(isWorking);
        setBusy(isBusy);
    }

    public byte getState()
    {
        return state;
    }

    public boolean getIsWorking()
    {
        return (state & 1) == 1;
    }

    public boolean getIsBusy()
    {
        return (state & (1 << 1)) == (1 << 1);
    }

    public void setState(byte state)
    {
        this.state = state;
    }

    public void setWorking(boolean value)
    {
        if (value)
        {
            state = (byte) (state | 1);
            return;
        }

        //11111110 bin = 126 dec
        state = (byte) (state & (Byte.MAX_VALUE - 1));
    }

    public void setBusy(boolean value)
    {
        if (!value)
        {
            //11111101 bin = 125 dec
            state = (byte) (state & (Byte.MAX_VALUE - (1 << 1)));
            return;
        }

        if (getIsWorking())
        {
            state = (byte) (state | (1 << 1));
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
        if (!(o instanceof ScheduleState)) return false;
        ScheduleState that = (ScheduleState) o;
        return state == that.state;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(state);
    }
}
