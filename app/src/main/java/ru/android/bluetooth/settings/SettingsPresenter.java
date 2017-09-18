package ru.android.bluetooth.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.CheckBox;

/**
 * Created by yasina on 18.09.17.
 */

public class SettingsPresenter {

    private static final String PREF_TIMEZONE  = "pref_timezone";
    private static final String PREF_LONGITUDE  = "pref_longitude";
    private static final String PREF_LATITUDE  = "pref_latitude";
    private Context mContext;

    public SettingsPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    public String[] getCoordinatesAndTimezone() {
        if (mContext == null) return null;

        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        return new String[]{
                sp.getString(PREF_LATITUDE, ""),
                sp.getString(PREF_LONGITUDE, ""),
                sp.getString(PREF_TIMEZONE, "")
        };
    }

    public void setCoordinatesAndTimezone(@Nullable double lon, @Nullable double lan, @Nullable String timeZone) {
        if (mContext == null || lon == 0 || lan == 0) return;
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        sp.edit().putString(PREF_LATITUDE, String.valueOf(lan)).apply();
        sp.edit().putString(PREF_LATITUDE, String.valueOf(lon)).apply();
        sp.edit().putString(PREF_TIMEZONE, timeZone).apply();
    }

    public void setCheckBoxLocation(final CheckBox checkButton, final TextInputLayout latitude, final TextInputLayout longitude){
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkButton.isChecked()){
                    latitude.setEnabled(true);
                    longitude.setEnabled(true);
                }else {
                    latitude.setEnabled(false);
                    longitude.setEnabled(false);
                }
            }
        });
    }

    public void setCheckBoxTimezone(final CheckBox checkButton, final TextInputLayout latitude){
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkButton.isChecked()){
                    latitude.setEnabled(true);
                }else {
                    latitude.setEnabled(false);
                }
            }
        });
    }
}
