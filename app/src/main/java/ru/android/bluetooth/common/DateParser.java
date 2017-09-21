package ru.android.bluetooth.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by yasina on 18.09.17.
 */

public class DateParser {

    public final String PREF_BEGIN_DAY = "begin_day_pref";
    private Calendar mCurrentDay;
    private Context mContext;

    public DateParser(Calendar mCurrentDay, Context mContext) {
        this.mCurrentDay = mCurrentDay;
        this.mContext = mContext;
    }

    public DateParser() {
    }

    public String setCorrectDateView(Calendar calendar){
        int month = mCurrentDay.get(Calendar.MONTH) + 1;
        return setZeros(mCurrentDay.get(Calendar.DAY_OF_MONTH)) + "."
                + setZeros(month) + "."
                + setZeros(mCurrentDay.get(Calendar.YEAR));
    }

    public String getDate(String dayNum){
        if(mCurrentDay == null){
            mCurrentDay = Calendar.getInstance();
            String result = setCorrectDateView(mCurrentDay);
            saveBeginDay(result);
            return result;
        }else {
            mCurrentDay.add(Calendar.DATE, 1);
            return setCorrectDateView(mCurrentDay);
        }
    }

    public String getTime(String time){
        try {
            time = time.replace("\r","");
            time = time.replaceAll("[^\\d.]", "");
            int timeMin = Integer.parseInt(time);
            String hour = setZeros(String.valueOf(timeMin / 60));
            String min = setZeros(String.valueOf(timeMin % 60));
            return hour + ":" + min;
        }catch (java.lang.NumberFormatException e){
            Log.d("ddd", "time = " + time);
        }
        return null;
    }

    public String setZeros(String time){
        if(time.length() == 1){
            time = "0" + time;
        }
        return time;
    }

    public String setZeros(int time){
        return setZeros(String.valueOf(time));
    }

    @Nullable
    public String getBeginDate() {
        if (mContext == null) return null;

        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        return sp.getString(PREF_BEGIN_DAY, "");
    }

    public void saveBeginDay(@Nullable String date) {
        if (mContext == null || date == null) return;
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        sp.edit().putString(PREF_BEGIN_DAY, date).apply();
    }

    public int getNumDate(String dayNum){
      return 0;
    }

    public int getNumTime(String time){
        try {
            int hour = Integer.parseInt(removeExtraZeros(time.substring(0, time.indexOf(":"))));
            int min = Integer.parseInt(removeExtraZeros(time.substring(time.indexOf(":")+1)));
            return hour*60 + min;
        }catch (java.lang.NumberFormatException e){
            Log.d("ddd", "time = " + time);
        }
        return -999;
    }

    private String removeExtraZeros(String value){
        if (value.indexOf(0) == '0'){
            value = value.substring(1);
        }
        return value;
    }
}
