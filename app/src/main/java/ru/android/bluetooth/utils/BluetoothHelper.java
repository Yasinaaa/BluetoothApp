package ru.android.bluetooth.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

/**
 * Created by yasina on 07.08.17.
 */

public class BluetoothHelper {

    public static final String PREF_ADDRESS = "pref_address_data";
    public static final String PREF_NAME = "pref_name_data";


    /*public static boolean isFirstLaunch(@Nullable Context context) {
        if (context == null) return false;

        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (sp.contains(PREF_ADDRESS) && sp.contains(PREF_NAME)) {
            return true;
        }
        return false;
    }*/
    @Nullable public static String[] getBluetoothUser(@Nullable final Context context) {
        if (context == null) return null;

        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return new String[]{
                sp.getString(PREF_ADDRESS, ""),
                sp.getString(PREF_NAME, "")
        };
    }

    public static void saveBluetoothUser(@Nullable final Context context, @Nullable String address, @Nullable String name) {
        if (context == null || address == null || name == null) return;
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        sp.edit().putString(PREF_ADDRESS, address).apply();
        sp.edit().putString(PREF_NAME, name).apply();
    }

    public static void clearPreferences(@Nullable Context context) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().clear().apply();
    }
}