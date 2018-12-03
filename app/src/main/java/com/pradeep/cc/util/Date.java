package com.pradeep.cc.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

public class Date implements Parcelable {
    private int day;
    private int month;
    private int year;

    public Date(int day) {
        this.day = day;
        Calendar c = Calendar.getInstance();
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
    }

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    protected Date(Parcel in) {
        day = in.readInt();
        month = in.readInt();
        year = in.readInt();
    }

    public static final Creator<Date> CREATOR = new Creator<Date>() {
        @Override
        public Date createFromParcel(Parcel in) {
            return new Date(in);
        }

        @Override
        public Date[] newArray(int size) {
            return new Date[size];
        }
    };

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return day + "-" + (month + 1) + "-" + year;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(day);
        dest.writeInt(month);
        dest.writeInt(year);
    }

    public long getTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime().getTime();
    }

    public long getZeroHourTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime().getTime();
    }

    public boolean equals(Date obj) {
        return (day == obj.day) && (month == obj.month) && (year == obj.year);
    }

    public void convertToPreviousMonth() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
    }

    public void convertToNextMonth() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
    }

    public static Date getTodaysDate() {
        Calendar c = Calendar.getInstance();
        return new Date(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR));
    }

    public Date getLastDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        int numOfDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth - 1);
        return new Date(calendar.get(Calendar.DAY_OF_MONTH), month, year);
    }
}
