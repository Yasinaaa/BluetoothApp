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
}
