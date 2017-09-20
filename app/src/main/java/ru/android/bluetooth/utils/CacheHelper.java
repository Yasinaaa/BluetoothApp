package ru.android.bluetooth.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

/**
 * Created by yasina on 19.09.17.
 */

public class CacheHelper {

    public static final String PREF_SCHEDULE_PATH = "schedule_path";
    private static final String PREF_TIMEZONE  = "pref_timezone";
    private static final String PREF_LONGITUDE  = "pref_longitude";
    private static final String PREF_LATITUDE  = "pref_latitude";

    @Nullable
    public static String getSchedulePath(@Nullable final Context context) {
        if (context == null) return null;

        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sp.getString(PREF_SCHEDULE_PATH, "");
    }

    public static void setSchedulePath(@Nullable final Context context, @Nullable String path) {
        if (context == null) return;
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        sp.edit().putString(PREF_SCHEDULE_PATH, path).apply();
    }

    @Nullable
    public static String[] getCoordinatesAndTimezone(@Nullable final Context context) {
        if (context == null) return null;

        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String[] result =  new String[]{
                sp.getString(PREF_LATITUDE, ""),
                sp.getString(PREF_LONGITUDE, ""),
                sp.getString(PREF_TIMEZONE, "")
        };
        if(result[0].equals("") & result[1].equals("") & result[0].equals("")){
            return null;
        }else
            return result;
    }

    public static void setCoordinatesAndTimezone(@Nullable final Context context, @Nullable double lon,
                                                 @Nullable double lan, @Nullable int timeZone) {
        if (context == null || lon == 0 || lan == 0) return;
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        sp.edit().putString(PREF_LATITUDE, String.valueOf(lan)).apply();
        sp.edit().putString(PREF_LONGITUDE, String.valueOf(lon)).apply();
        sp.edit().putString(PREF_TIMEZONE, String.valueOf(timeZone)).apply();
    }
}
