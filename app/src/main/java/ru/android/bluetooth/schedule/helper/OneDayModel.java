package ru.android.bluetooth.schedule.helper;

import java.util.Calendar;

/**
 * Created by yasina on 22.08.17.
 */

public class OneDayModel {

    private String day;
    private String onMinutes;
    private String offMinutes;

    public OneDayModel(String day, String onMinutes, String offMinutes) {
        this.day = day;
        this.onMinutes = onMinutes;
        this.offMinutes = offMinutes;
    }

    private String getDate(Calendar date){
        return date.get(Calendar.DAY_OF_MONTH) + "." + date.get(Calendar.MONTH) + "." + date.get(Calendar.YEAR);
    }

    public OneDayModel(Calendar date, String text) {
        this.day = getDate(date);
                //text.substring(text.indexOf("=") + 1, text.indexOf(","));
        text = text.substring(text.indexOf(",")+1);
        this.onMinutes = text.substring(0, text.indexOf(","));
        this.offMinutes = text.substring(text.lastIndexOf(",")+1);
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getOnMinutes() {
        return onMinutes;
    }

    public void setOnMinutes(String onMinutes) {
        this.onMinutes = onMinutes;
    }

    public String getOffMinutes() {
        return offMinutes;
    }

    public void setOffMinutes(String offMinutes) {
        this.offMinutes = offMinutes;
    }
}