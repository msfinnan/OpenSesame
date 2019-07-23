package com.margi.sesame;

import android.util.Log;

import org.joda.time.LocalTime;

public class TimeRange {
    private LocalTime startTime;
    private LocalTime endTime;


//note - there is an interval in joda time

    public TimeRange(int startHour, int startMinute, int endHour, int endMinute) {
        this.startTime = new LocalTime(startHour, startMinute);
        this.endTime = new LocalTime(endHour, endMinute);
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    //overload - can pass rangeIncludes localtime OR int hour and int minute
    public boolean rangeIncludes(int hour, int minute) {
        return rangeIncludes(new LocalTime(hour, minute));
    }

    public boolean rangeIncludes(LocalTime requestedTime) {
        //returns a bool
        return requestedTime.isAfter(startTime) && requestedTime.isBefore(endTime);
    }
}


