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


    private Context mContext;

    public SettingsPresenter(Context mContext) {
        this.mContext = mContext;
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
