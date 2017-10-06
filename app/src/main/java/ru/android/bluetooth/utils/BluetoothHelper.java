package ru.android.bluetooth.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by yasina on 07.08.17.
 */

public class BluetoothHelper {

    public static final String PREF_ADDRESS = "pref_address_data";
    public static final String PREF_NAME = "pref_name_data";
    public static final String PREF_OPEN = "pref_open";

    @Nullable public static String[] getBluetoothUser(@Nullable final Context context) {
        if (context == null) return null;

        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return new String[]{
                sp.getString(PREF_ADDRESS, ""),
                sp.getString(PREF_NAME, "")
        };
    }

    public static void saveBluetoothOpened(@Nullable final Context context, @Nullable boolean isOpen) {
        if (context == null) return;
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        sp.edit().putBoolean(PREF_OPEN, isOpen).apply();
    }

    @Nullable public static boolean isOpen(@Nullable final Context context) {
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sp.getBoolean(PREF_OPEN,false);
    }

    public static void saveBluetoothUser(@Nullable final Context context, @Nullable String address, @Nullable String name) {
        if (context == null || address == null || name == null) return;
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        sp.edit().putString(PREF_ADDRESS, address).apply();
        sp.edit().putString(PREF_NAME, name).apply();
    }

    private static SharedPreferences getSharedPreferences(@NonNull final Context context) {
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sp;
    }

    public static void saveBluetoothDeviceTitle(@NonNull final Context context, @Nullable String name){
        getSharedPreferences(context).edit().putString(PREF_NAME, name).apply();
    }

    public static void clearPreferences(@Nullable Context context) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().clear().apply();
    }
}
