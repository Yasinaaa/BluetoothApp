package ru.android.autorele.common.date_time;

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

    private final String TAG = "DateParser";
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
                + setZeros(month);
    }

    public String getDate(){
        String result = setCorrectDateView(mCurrentDay);
        mCurrentDay.add(Calendar.DATE, 1);
        return result;
    }

    public String getTime(String time){
        try {
            try {
                if (time.length() == 1){
                    return "00:0" + time;
                }else {
                    time = time.replace("\r","");
                    time = time.replaceAll("[^\\d.]", "");
                    int timeMin = Integer.parseInt(time);
                    String hour = setZeros(String.valueOf(timeMin / 60));
                    String min = setZeros(String.valueOf(timeMin % 60));
                    return hour + ":" + min;
                }

            }catch (NullPointerException e){
                Log.d(TAG, "time=" + time);
            }

        }catch (java.lang.NumberFormatException e){
            Log.d(TAG, "time = " + time);
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

    public int getNumTime(String time){
        try {
            if (time.contains(":")) {
                int hour = Integer.parseInt(removeExtraZeros(time.substring(0, time.indexOf(":"))));
                int min = Integer.parseInt(removeExtraZeros(time.substring(time.indexOf(":") + 1)));
                return hour * 60 + min;
            }
        }catch (java.lang.NumberFormatException e){
            Log.d(TAG, "time = " + time);
        }
        return -999;
    }

    public String getTime(int time){
        try {
            int hour = time/60;
            int min = time%60;
            return setZeros(hour) + ":" + setZeros(min);

        }catch (java.lang.NumberFormatException e){
            Log.d(TAG, "time = " + time);
        }
        return null;
    }

    private String removeExtraZeros(String value){
        if (value.indexOf(0) == '0'){
            value = value.substring(1);
        }
        return value;
    }

    public void setNewCurrentDay() {
        mCurrentDay = Calendar.getInstance();
        mCurrentDay.set(Calendar.DAY_OF_MONTH, 1);
        mCurrentDay.set(Calendar.MONTH, Calendar.JANUARY);
    }
}
