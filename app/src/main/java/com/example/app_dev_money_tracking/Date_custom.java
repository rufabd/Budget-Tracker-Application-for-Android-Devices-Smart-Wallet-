package com.example.app_dev_money_tracking;

import androidx.annotation.NonNull;

class Date_custom
{
    String year;
    String month;
    String day;

    public Date_custom(String year, String month, String day)
    {
        this.year = year;
        setMonth(month);
        setDay(day);
    }

    public Date_custom(int year, int month, int day)
    {
        this.year = "" + year;
        setMonth("" + month);
        setDay("" + day);
    }

    public String getYear()
    {
        return year;
    }

    public void setYear(String year)
    {
        this.year = year;
    }

    public String getMonth()
    {
        return month;
    }

    public String getMonthNormal()
    {
        int int_mn = Integer.parseInt(month);
        int_mn++;
        String ret = int_mn + "";
        ret.trim();
        if (ret.length() == 1) {
            ret = "0" + month;
        }
        return ret;
    }

    public void setMonth(String mon)
    {
        this.month = mon + "";
        month.trim();
        if (this.month.length() == 1) {
            this.month = "0" + month;
        }
    }

    public String getDay()
    {
        return day;
    }

    public void setDay(String day)
    {
        this.day = day;
        day.trim();
        if (this.day.length() == 1) {
            this.day = "0" + day;
        }
    }

    @NonNull
    @Override
    public String toString()
    {
        return getYear() + "-" + getMonthNormal() + "-" + getDay();
    }
}
